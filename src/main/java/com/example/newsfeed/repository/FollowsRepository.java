package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Follows;
import com.example.newsfeed.entity.FollowsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;   // 추가
import org.springframework.data.jpa.repository.Query;       // 추가
import org.springframework.data.repository.query.Param;     // 추가

import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, FollowsId> {

    // 단방향 정확 매칭
    Optional<Follows> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 내가 팔로우 중(=follower) + 수락 상태 목록
    List<Follows> findByFollowerIdAndStatus(Long followerId, Boolean status);

    // 양방향 중 하나라도 존재하면 1건 가져오기
    @Query("""
        select f
        from Follows f
        where (f.followerId = :a and f.followingId = :b)
           or (f.followerId = :b and f.followingId = :a)
        """)
    Optional<Follows> findFriendshipBetween(@Param("a") Long a, @Param("b") Long b);
}
