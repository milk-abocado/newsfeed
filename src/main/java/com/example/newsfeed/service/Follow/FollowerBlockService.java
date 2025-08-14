package com.example.newsfeed.service.Follow;

import com.example.newsfeed.dto.Follow.BlockRequestDto;
import com.example.newsfeed.dto.Follow.BlockResponseDto;
import com.example.newsfeed.entity.User.BlockedUser;
import com.example.newsfeed.repository.Follow.FollowerBlockRepository;
import com.example.newsfeed.repository.Follow.FollowsRepository;
import com.example.newsfeed.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerBlockService {

    private final FollowerBlockRepository followerBlockRepository;
    private final FollowsRepository followsRepository;   // 차단 시 관계 정리에 사용
    private final UserRepository userRepository;         // 대상 존재 검증

    /**
     * [1] 사용자 차단
     * - 이미 차단한 경우 예외 발생
     * - 차단 정보 저장
     * - 친구/대기중 요청 포함 모든 관계(양방향) 정리
     */
    @Transactional
    public String blockUser(Long userId, BlockRequestDto requestDto) {
        // ---- 방어코드 ----
        if (requestDto == null || requestDto.getTargetUserId() == null) {
            throw new IllegalArgumentException("요청 데이터가 올바르지 않습니다.");
        }

        Long targetId = requestDto.getTargetUserId();

        if (userId.equals(targetId)) {
            throw new IllegalStateException("자기 자신은 차단할 수 없습니다.");
        }

        // 대상 존재 검증
        if (!userRepository.existsById(targetId)) {
            throw new IllegalStateException("대상 사용자를 찾을 수 없습니다.");
        }

        // 이미 차단했는지 검사
        if (followerBlockRepository.existsByUserIdAndTargetUserId(userId, targetId)) {
            throw new IllegalStateException("이미 차단한 사용자입니다.");
        }

        // 1) 차단 레코드 저장
        followerBlockRepository.save(
                BlockedUser.builder()
                        .userId(userId)          // 차단한 사람
                        .targetUserId(targetId)  // 차단당한 사람
                        .build()
        );

        // 2) 친구/요청 관계 전부 삭제(양방향)
        followsRepository.deleteAnyRelationBetween(userId, targetId);

        return "사용자가 성공적으로 차단되었습니다.";
    }

    /**
     * [2] 사용자 차단 해제
     * - 해당 사용자 차단 기록이 없는 경우 예외 발생
     * - 차단 기록 삭제
     * - 관계는 자동 복구하지 않음(정책)
     */
    @Transactional
    public String unblockUser(Long userId, BlockRequestDto requestDto) {
        // ---- 방어코드 ----
        if (requestDto == null || requestDto.getTargetUserId() == null) {
            throw new IllegalArgumentException("요청 데이터가 올바르지 않습니다.");
        }

        Long targetId = requestDto.getTargetUserId();

        Optional<BlockedUser> blockedUserOptional =
                followerBlockRepository.findByUserIdAndTargetUserId(userId, targetId);

        if (blockedUserOptional.isEmpty()) {
            throw new IllegalStateException("사용자가 차단되지 않았습니다.");
        }

        followerBlockRepository.delete(blockedUserOptional.get());
        return "사용자 차단이 성공적으로 해제되었습니다.";
    }

    /**
     * [3] 차단한 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BlockResponseDto> getBlockedUsers(Long userId) {
        return followerBlockRepository.findBlockedUsersByUserId(userId);
    }

    /**
     * [4] 단방향 차단 여부 확인 (내가 그를 차단했는가)
     */
    @Transactional(readOnly = true)
    public boolean isBlocked(Long userId, Long targetUserId) {
        return followerBlockRepository.existsByUserIdAndTargetUserId(userId, targetUserId);
    }

    /**
     * [5] 양방향 차단 여부 확인
     * - a가 b를 차단했거나, b가 a를 차단했으면 true
     * - 팔로우/친구 요청/수락 등에서 가드로 사용
     */
    @Transactional(readOnly = true)
    public boolean isBlockedEitherDirection(Long a, Long b) {
        // 단일 쿼리 버전을 쓰고 싶으면 existsEitherDirectionQuery로 교체 가능
        return followerBlockRepository.existsEitherDirection(a, b);
        // return followerBlockRepository.existsEitherDirectionQuery(a, b);
    }
}
