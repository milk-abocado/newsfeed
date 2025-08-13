package com.example.newsfeed.service;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.entity.UserBlock;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.FollowerBlockRepository;
import com.example.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerBlockService {
    private final FollowerBlockService followerBlockService;
    private final FollowerBlockRepository followerBlockRepository;
    private final UserRepository userRepository;

    //1.사용자 차단
    public String blockUser(Long userId, BlockRequestDto requestDto) {
        if (FollowerBlockRepository.existsByUserIdAndTargetUserId(userId, requestDto.getTargetUserId())) {
            throw new IllegalStateException("이미 차단한 사용자입니다."); //중복 차단 불가(예외)
        }

        UserBlock blockedUser = UserBlock.builder()
                .userId(userId) //차단한 사람
                .targetUserId(requestDto.getTargetUserId()) //차단당한 사람
                .build();
        followerBlockRepository.save(blockedUser);
        return "사용자가 성공적으로 차단되었습니다.";
    }

    //2.사용자 차단 해제
    //2-1)해당 사용자 검색
    public String unblockUser(Long userId, BlockRequestDto requestDto) {
        Optional<UserBlock> blockedUserOptional =
                followerBlockRepository.findByUserIdAndTargetUserId(userId, requestDto.getTargetUserId());
        //2-2)예외(차단 기록 X)
        if (blockedUserOptional.isEmpty()) {
            throw new IllegalStateException("사용자가 차단되지 않았습니다.");
        }
        followerBlockRepository.delete(blockedUserOptional.get());
        return "사용자 차단이 성공적으로 해제되었습니다.";
    }

    //3.차단한 사용자 목록 조회
    public List<UserBlock> getBlockedUsers(Long userId) {
        return followerBlockRepository.findAllByUserId(userId);
    }

    //4.특정 사용자 차단 여부 확인
    public boolean isBlocked(Long userId, Long targetUserId) {
        return FollowerBlockRepository.existsByUserIdAndTargetUserId(userId, targetUserId);
    }

    //username -> userId 매핑 (principal.getName()이 username일 때 사용)
    @Transactional(readOnly = true)
    public Long getUserIdByUsername(String name) {
        return userRepository.findByUsername(name)
                .map(Users::getId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}