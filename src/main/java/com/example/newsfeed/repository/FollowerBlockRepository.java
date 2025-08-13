package com.example.newsfeed.repository;

import com.example.newsfeed.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowerBlockRepository extends JpaRepository<UserBlock, Long> {
    Optional<UserBlock> findByUserIdAndTargetUserId(Long userId, Long targetUserId);
    //userId가 특정값인 모든 BlockedUser Entity 조회
    List<UserBlock> findAllByUserId(Long userId); //userId가 특정값인 모든 BlockedUser Entity 조회

    //userId와 targetUserId가 모두 일치하는 레코드 존재하는지 여부 확인
    static boolean existsByUserIdAndTargetUserId(Long userId, Long targetUserId) {
        return false;
    }
}
