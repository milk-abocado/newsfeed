package com.example.newsfeed.service.Follow;

import com.example.newsfeed.dto.Follow.FollowListDto;
import com.example.newsfeed.dto.Follow.FollowRequestDto;
import com.example.newsfeed.dto.Follow.FollowResponseDto;
import com.example.newsfeed.entity.Follow.Follows;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.repository.Follow.FollowerBlockRepository;
import com.example.newsfeed.repository.Follow.FollowsRepository;
import com.example.newsfeed.repository.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;
    private final FollowerBlockRepository followerBlockRepository;

    // ===== 팔로우(친구) 요청 보내기 =====
    @Transactional
    public FollowResponseDto sendFollowRequest(FollowRequestDto followRequestDto, Long followerId) {
        Long targetId = followRequestDto.getFollowId();
        if (Objects.equals(followerId, targetId)) {
            throw new IllegalArgumentException("자기 자신에게는 요청할 수 없습니다.");
        }

        Users follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우하는 사용자를 찾을 수 없습니다."));
        Users following = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우되는 사용자를 찾을 수 없습니다."));

        // 차단 가드
        if (followerBlockRepository.existsEitherDirection(followerId, targetId)) {
            throw new IllegalStateException("차단 상태에서는 친구/팔로우 요청을 보낼 수 없습니다.");
        }

        // === 방향별 검사로 변경: followerId -> targetId 만 봄 ===
        // 같은 방향으로 이미 팔로우(수락 true)인 경우
        if (followsRepository.existsByFollowerIdAndFollowingIdAndStatus(followerId, targetId, true)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다."); // (필요시 "이미 친구 상태입니다."로 유지 가능)
        }
        // 같은 방향으로 이미 대기(false)인 경우
        if (followsRepository.existsByFollowerIdAndFollowingIdAndStatus(followerId, targetId, false)) {
            throw new IllegalArgumentException("이미 대기 중인 요청이 있습니다.");
        }

        // 반대 방향에 true가 있더라도(=상대가 이미 나를 팔로우 중이더라도) 맞팔 시도는 허용!
        // -> 별도 요청을 보내고, 상대가 수락하면 맞팔 완성

        Follows follow = Follows.builder()
                .followerId(followerId)
                .followingId(targetId)
                .status(false)  // 대기
                .follower(follower)
                .following(following)
                .build();

        followsRepository.save(follow);
        return new FollowResponseDto(follow.getFollowerId(), follow.getFollowingId(), follow.getStatus());
    }

    // ===== 친구 수락 =====
    @Transactional
    public void acceptFollowRequest(Long followerId, Long followingId) {
        // followerId -> followingId 로 들어온 '대기중' 요청을 수락
        Follows follow = followsRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 없습니다."));

        if (Boolean.TRUE.equals(follow.getStatus())) {
            throw new IllegalArgumentException("이미 수락된 요청입니다.");
        }

        // 수락 직전에도 차단 상태면 불가(양방향)
        if (followerBlockRepository.existsEitherDirection(followerId, followingId)) {
            throw new IllegalStateException("차단 상태에서는 친구 요청을 수락할 수 없습니다.");
        }

        follow.setStatus(true);
        // save 호출 없이도 @Transactional이면 dirty checking으로 반영되지만, 명시 저장을 유지해도 무방
        followsRepository.save(follow);
    }

    // ===== 친구 요청 거절 =====
    @Transactional
    public void rejectFollowRequest(Long followerId, Long followingId) {
        Follows follow = followsRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 없습니다."));

        if (Boolean.TRUE.equals(follow.getStatus())) {
            throw new IllegalArgumentException("이미 수락된 친구 관계입니다. 삭제가 필요합니다.");
        }

        // 대기 중 요청은 삭제로 처리
        followsRepository.delete(follow);
    }

    // ===== 친구 삭제 (수락 true인 한 줄 삭제) =====
    @Transactional
    public void deleteFriend(Long currentUserId, Long targetUserId) {
        if (Objects.equals(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("본인 아이디는 삭제할 수 없습니다.");
        }

        // 방향 고정: 내가 팔로우한 줄만 찾기
        Follows follow = followsRepository
                .findByFollowerIdAndFollowingId(currentUserId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("친구가 아닙니다.")); // 또는 "팔로우 중이 아닙니다."

        if (!Boolean.TRUE.equals(follow.getStatus())) {
            throw new IllegalArgumentException("대기 중인 친구 요청은 삭제할 수 없습니다.");
        }

        followsRepository.delete(follow);
    }

    // ===== 친구 리스트 조회 (내가 팔로우 중 & 수락 true) =====
    @Transactional
    public List<FollowListDto> getFriendList(Long userId) {
        List<Follows> follows = followsRepository.findByFollowerIdAndStatus(userId, true);
        return follows.stream()
                .map(f -> new FollowListDto(f.getFollowing().getId(), f.getFollowing().getNickname()))
                .collect(Collectors.toList());
    }
}
