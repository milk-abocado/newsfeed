package com.example.newsfeed.service;

import com.example.newsfeed.dto.FollowListDto;
import com.example.newsfeed.dto.FollowRequestDto;
import com.example.newsfeed.dto.FollowResponseDto;
import com.example.newsfeed.entity.Follows;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.FollowsRepository;
import com.example.newsfeed.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    // 팔로우 요청 처리
    public FollowResponseDto sendFollowRequest(FollowRequestDto followRequestDto, Long followerId) {
        Users follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우하는 사용자를 찾을 수 없습니다."));

        Users following = userRepository.findById(followRequestDto.getFollowId())
                .orElseThrow(() -> new IllegalArgumentException("팔로우되는 사용자를 찾을 수 없습니다."));

        // 팔로우 관계 저장 (대기 상태로)
        Follows follow = Follows.builder()
                .followerId(followerId)
                .followingId(following.getId())
                .status(false)  // 대기 상태
                .follower(follower)
                .following(following)
                .build();

        followsRepository.save(follow);

        return new FollowResponseDto(follow.getFollowerId(), follow.getFollowingId(), follow.getStatus());
    }

    // 친구 수락하기
    public void acceptFollowRequest(Long followerId, Long followingId) {
        // 친구 요청을 찾고 수락 상태로 변경
        Follows follow = followsRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 없습니다."));

        // 상태를 '수락'으로 변경
        if (follow.getStatus()) {
            throw new IllegalArgumentException("이미 수락된 요청입니다.");
        }

        follow.setStatus(true);  // 수락 상태로 변경
        followsRepository.save(follow);
    }

    // 친구 요청 거절하기
    public void rejectFollowRequest(Long followerId, Long followingId) {
        // 친구 요청을 찾고 거절 상태로 변경
        Follows follow = followsRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 없습니다."));

        // 친구 요청 상태가 수락되었으면, 거절은 불가능
        if (follow.getStatus()) {
            throw new IllegalArgumentException("이미 수락된 친구 관계입니다. 삭제가 필요합니다.");
        }

        // 친구 요청이 대기 중인 상태에서는, 관계를 삭제하는 것으로 처리
        followsRepository.delete(follow);  // 관계 종료 (친구 삭제)
    }

    // 친구 삭제하기 (양방향 중 존재하는 줄을 찾아 삭제)
    @Transactional
    public void deleteFriend(Long currentUserId, Long targetUserId) {
        Follows follow = followsRepository
                .findFriendshipBetween(currentUserId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("친구가 아닙니다."));
        if (!Boolean.TRUE.equals(follow.getStatus()))
            throw new IllegalArgumentException("대기 중인 친구 요청은 삭제할 수 없습니다.");

        followsRepository.delete(follow);
    }

    // 친구 리스트 조회
    public List<FollowListDto> getFriendList(Long userId) {
        // 수락된 친구 목록만 조회
        List<Follows> follows = followsRepository.findByFollowerIdAndStatus(userId, true);

        // friendId, friendName을 DTO로 반환
        return follows.stream()
                .map(follow -> {
                    Users friend = follow.getFollowing();
                    return new FollowListDto(friend.getId(), friend.getNickname());
                })
                .collect(Collectors.toList());
    }
}
