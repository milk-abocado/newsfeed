package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Follows;
import com.example.newsfeed.entity.FollowsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, FollowsId> {

    // 특정 팔로워와 팔로잉 관계를 조회
    // @param followerId: 팔로워의 ID
    // @param followingId: 팔로잉의 ID
    // @return: 팔로워와 팔로잉 관계에 해당하는 Follows 객체 (없으면 Optional.empty())
    Optional<Follows> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 팔로워의 ID와 팔로우 상태(true: 수락된 친구)로 친구 목록을 조회
    // @param followerId: 팔로워의 ID
    // @param status: 친구 요청 상태 (TRUE: 수락된 상태)
    // @return: 팔로우 상태가 'true'인 친구들을 포함한 Follows 객체 리스트
    List<Follows> findByFollowerIdAndStatus(Long followerId, Boolean status);
}
