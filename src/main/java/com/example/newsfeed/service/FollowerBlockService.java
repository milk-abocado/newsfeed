package com.example.newsfeed.service;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.entity.BlockedUser;
import com.example.newsfeed.repository.FollowerBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerBlockService {
    private final FollowerBlockService followerBlockService;
    private final FollowerBlockRepository followerBlockRepository;

    //1.사용자 차단
    public String blockUser(Long userId, BlockRequestDto requestDto) {
        if (FollowerBlockRepository.existsByUserIdAndTargetUserId(userId, requestDto.getTargetUserId())) {
            throw new IllegalStateException("이미 차단한 사용자입니다."); //예외
        }

        BlockedUser blockedUser = BlockedUser.builder()
                .userId(userId) //차단한 사람
                .targetUserId(requestDto.getTargetUserId()) //차단당한 사람
                .build();
        followerBlockRepository.save(blockedUser);
        return "사용자가 성공적으로 차단되었습니다.";
    }

    //2.사용자 차단 해제
    //2-1)해당 사용자 검색
    public String unblockUser(Long userId, BlockRequestDto requestDto) {
        Optional<BlockedUser> blockedUserOptional =
                followerBlockRepository.findByUserIdAndTargetUserId(userId, requestDto.getTargetUserId());
        //2-2)예외(차단 기록 X)
        if (blockedUserOptional.isEmpty()) {
            throw new IllegalStateException("사용자가 차단되지 않았습니다.");
        }

    //3.차단 목록 조회
    public List<BlockedUser> getBlockedUsers(Long userId) {
        return followerBlockRepository.findAllByUserId(userId);
    }

    //4.차단 여부 확인
    public boolean isBlocked(Long userId, Long targetUserId) {
        return FollowerBlockRepository.existsByUserIdAndTargetUserId(userId, targetUserId);
    }
}
