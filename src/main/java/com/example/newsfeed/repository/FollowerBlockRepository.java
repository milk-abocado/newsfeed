package com.example.newsfeed.repository;

import com.example.newsfeed.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerBlockRepository extends JpaRepository<BlockedUser, Long> {
    //userId가 특정값인 모든 BlockedUser Entity 조회
    List<BlockedUser> findAllByUserId(Long userId);

    Optional<BlockedUser> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    //userId와 targetUserId가 모두 일치하는 레코드 존재하는지 여부 확인
    static boolean existsByUserIdAndTargetUserId(Long userId, Long targetUserId) {
        return false;
    }
}
