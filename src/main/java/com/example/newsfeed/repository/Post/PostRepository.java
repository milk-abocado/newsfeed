package com.example.newsfeed.repository.Post;

import com.example.newsfeed.entity.Post.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {

    // ===== 단건 조회: 삭제되지 않은 게시물만 =====
    Optional<Posts> findByIdAndIsDeletedFalse(Long id);

    // ===== 뉴스피드 기본 정렬 (viewer 포함 userIds 범위 + 기간)
    // 양방향 차단 제외(내가 차단했거나, 상대가 나를 차단했거나)
    @Query("""
           SELECT p FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :viewerId
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :viewerId
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedByUserIdsAndDateRange(
            @Param("viewerId") Long viewerId,
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // ===== 뉴스피드 좋아요 많은 순 (범위+기간)
    // 양방향 차단 제외
    @Query(value = """
           SELECT p
           FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :viewerId
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :viewerId
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           ORDER BY (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p) DESC,
                    p.updatedAt DESC
           """,
            countQuery = """
           SELECT COUNT(p)
           FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :viewerId
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :viewerId
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedByUserIdsAndDateRangeOrderByLikeCountDesc(
            @Param("viewerId") Long viewerId,
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    /**
     * 기간 필터 + 기본 정렬(페이지 정렬 사용)
     * + 내가 숨긴 글(PostHide) 제외
     * + 양방향 차단 제외
     * - userIds: 내 ID + 내가 팔로우한 사용자 IDs
     * - me: 현재 로그인 사용자 ID
     */
    @Query("""
           SELECT p FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM PostHide h
               WHERE h.user.id = :me
                 AND h.post.id = p.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :me
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :me
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedExcludingHiddenByUserIdsAndDateRange(
            @Param("me") Long me,
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    /**
     * 기간 필터 + 좋아요 많은 순
     * + 내가 숨긴 글(PostHide) 제외
     * + 양방향 차단 제외
     */
    @Query(value = """
           SELECT p
           FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM PostHide h
               WHERE h.user.id = :me
                 AND h.post.id = p.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :me
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :me
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           ORDER BY (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p) DESC,
                    p.updatedAt DESC
           """,
            countQuery = """
           SELECT COUNT(p)
           FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND NOT EXISTS (
               SELECT 1 FROM PostHide h
               WHERE h.user.id = :me
                 AND h.post.id = p.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b1
               WHERE b1.userId = :me
                 AND b1.targetUserId = p.user.id
             )
             AND NOT EXISTS (
               SELECT 1 FROM BlockedUser b2
               WHERE b2.userId = p.user.id
                 AND b2.targetUserId = :me
             )
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedExcludingHiddenByUserIdsAndDateRangeOrderByLikeCountDesc(
            @Param("me") Long me,
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // ===== 게시물 전체 조회: 내가 숨긴 글 + 양방향 차단 제외
    @Query("""
       SELECT p
       FROM Posts p
       WHERE p.isDeleted = false
         AND NOT EXISTS (
           SELECT 1 FROM PostHide h
           WHERE h.user.id = :me
             AND h.post.id = p.id
         )
         AND NOT EXISTS (
           SELECT 1 FROM BlockedUser b1
           WHERE b1.userId = :me
             AND b1.targetUserId = p.user.id
         )
         AND NOT EXISTS (
           SELECT 1 FROM BlockedUser b2
           WHERE b2.userId = p.user.id
             AND b2.targetUserId = :me
         )
       """)
    Page<Posts> findAllVisibleToUser(@Param("me") Long me, Pageable pageable);

    // ===== (추가) 팔로우 집합만 주는 버전: 양방향 차단 제외 (정렬은 pageable에서 지정)
    @Query("""
        select p from Posts p
        where p.isDeleted = false
          and p.user.id in :visibleAuthorIds
          and p.user.id not in (
              select bu.targetUserId from BlockedUser bu where bu.userId = :viewerId
          )
          and p.user.id not in (
              select bu.userId from BlockedUser bu where bu.targetUserId = :viewerId
          )
    """)
    Page<Posts> findFeedExcludingBlocked(@Param("viewerId") Long viewerId,
                                         @Param("visibleAuthorIds") Collection<Long> visibleAuthorIds,
                                         Pageable pageable);
}
