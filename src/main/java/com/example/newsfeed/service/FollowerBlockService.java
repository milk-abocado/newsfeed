package com.example.newsfeed.service;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.dto.BlockResponseDto;
import com.example.newsfeed.entity.BlockedUser;
import com.example.newsfeed.repository.FollowerBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerBlockService {
    private final FollowerBlockRepository followerBlockRepository;

    /**
     * [1] 사용자 차단
     * - 이미 차단한 경우 예외 발생
     * - 차단 정보 저장
     *
     * @param userId     차단을 수행하는 사용자 ID
     * @param requestDto 차단 대상 ID가 포함된 요청 DTO
     * @return 차단 성공 메시지
     */
    public String blockUser(Long userId, BlockRequestDto requestDto) {
        // 이미 차단한 사용자라면 예외 발생
        if (followerBlockRepository.existsByUserIdAndTargetUserId(userId, requestDto.getTargetUserId())) {
            throw new IllegalStateException("이미 차단한 사용자입니다.");
        }

        // 차단 엔티티 생성 및 저장
        BlockedUser blockedUser = BlockedUser.builder()
                .userId(userId) // 차단한 사람
                .targetUserId(requestDto.getTargetUserId()) // 차단당한 사람
                .build();

        followerBlockRepository.save(blockedUser);
        return "사용자가 성공적으로 차단되었습니다.";
    }

    /**
     * [2] 사용자 차단 해제
     * - 해당 사용자 차단 기록이 없는 경우 예외 발생
     * - 차단 기록 삭제
     *
     * @param userId     차단 해제를 수행하는 사용자 ID
     * @param requestDto 차단 해제 대상 ID가 포함된 요청 DTO
     * @return 차단 해제 성공 메시지
     */
    public String unblockUser(Long userId, BlockRequestDto requestDto) {
        // 차단 기록 조회
        Optional<BlockedUser> blockedUserOptional =
                followerBlockRepository.findByUserIdAndTargetUserId(userId, requestDto.getTargetUserId());

        // 차단 기록이 없으면 예외 발생
        if (blockedUserOptional.isEmpty()) {
            throw new IllegalStateException("사용자가 차단되지 않았습니다.");
        }

        // 차단 기록 삭제
        followerBlockRepository.delete(blockedUserOptional.get());
        return "사용자 차단이 성공적으로 해제되었습니다.";
    }

    /**
     * [3] 차단한 사용자 목록 조회
     *
     * @param userId 차단한 사람의 ID
     * @return 차단한 사용자 목록 DTO 리스트
     */
    public List<BlockResponseDto> getBlockedUsers(Long userId) {
        return followerBlockRepository.findBlockedUsersByUserId(userId);
    }

    /**
     * [4] 특정 사용자 차단 여부 확인
     *
     * @param userId       차단한 사람의 ID
     * @param targetUserId 차단 여부를 확인할 대상 ID
     * @return true = 차단 상태, false = 차단 아님
     */
    public boolean isBlocked(Long userId, Long targetUserId) {
        return followerBlockRepository.existsByUserIdAndTargetUserId(userId, targetUserId);
    }
}