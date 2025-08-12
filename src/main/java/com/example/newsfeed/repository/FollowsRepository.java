package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Follows;
import com.example.newsfeed.entity.FollowsId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;       // 추가
import org.springframework.data.repository.query.Param;     // 추가

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, FollowsId> {

    // === 단건 조회 ===
    Optional<Follows> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 내가 팔로우 중(= follower) + 상태 필터(accepted/pending)
    List<Follows> findByFollowerIdAndStatus(Long followerId, Boolean status);

    // 팔로우 중(=following) + 수락 상태 목록
    List<Follows> findByFollowingIdAndStatus(Long followingId, Boolean status);

    // 두 사용자 사이에 어느 방향이든 관계가 있으면 가져오기(대기/수락 구분은 엔티티 status로 확인)
    @Query("""
        select f
        from Follows f
        where (f.followerId = :a and f.followingId = :b)
           or (f.followerId = :b and f.followingId = :a)
        """)
    Optional<Follows> findFriendshipBetween(@Param("a") Long a, @Param("b") Long b);

    // === ID 리스트 조회 (목록/뉴스피드용) ===
    // 내가 팔로우하는 사용자 ID들
    @Query("select f.followingId from Follows f where f.followerId = :followerId and (:status is null or f.status = :status)")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId,
                                            @Param("status") Boolean status);

    // 나를 팔로우하는 사용자 ID들
    @Query("select f.followerId from Follows f where f.followingId = :followingId and (:status is null or f.status = :status)")
    List<Long> findFollowerIdsByFollowingId(@Param("followingId") Long followingId,
                                            @Param("status") Boolean status);

    // === 존재 여부/카운트 ===
    boolean existsByFollowerIdAndFollowingIdAndStatus(Long followerId, Long followingId, Boolean status);

    // === 배치 관계 조회 (N+1 방지: 한 번에 플래그 계산) ===
    // viewer -> targets
    @Query("select f from Follows f where f.followerId = :viewerId and f.followingId in :targetIds")
    List<Follows> findAllByFollowerIdAndFollowingIdIn(@Param("viewerId") Long viewerId,
                                                      @Param("targetIds") Collection<Long> targetIds);

    // targets -> viewer
    @Query("select f from Follows f where f.followerId in :targetIds and f.followingId = :viewerId")
    List<Follows> findAllByFollowerIdInAndFollowingId(@Param("targetIds") Collection<Long> targetIds,
                                                      @Param("viewerId") Long viewerId);

    // ===== 목록 조회: 프로젝션 반환 =====

    // 팔로잉(내가 팔로우하는 사용자들)
    @Query("""
        select u.id as id, u.nickname as nickname, u.profileImage as profileImage
        from Follows f
          join f.following u
        where f.followerId = :userId
          and f.status = :status
    """)
    Page<UserSummary> findFollowingUsers(@Param("userId") Long userId,
                                         @Param("status") Boolean status,
                                         Pageable pageable);

    // 팔로워(나를 팔로우하는 사용자들)
    @Query("""
        select u.id as id, u.nickname as nickname, u.profileImage as profileImage
        from Follows f
          join f.follower u
        where f.followingId = :userId
          and f.status = :status
    """)
    Page<UserSummary> findFollowerUsers(@Param("userId") Long userId,
                                        @Param("status") Boolean status,
                                        Pageable pageable);
}
