package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {

    // 삭제되지 않은 게시물만 단건 조회
    Optional<Posts> findByIdAndIsDeletedFalse(Long id);

    // 삭제되지 않은 게시물만 전체 조회
    List<Posts> findAllByIsDeletedFalse();

    // 팔로잉 + 본인 게시물 범위 조회 (정렬은 Pageable로 지정: 예) updatedAt DESC)
    // 작성일(createdAt) 기간 필터: [start, end)
    @Query("""
           SELECT p FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // 좋아요 많은 순 (작성일 범위 + 좋아요 수 DESC + 보조정렬 updatedAt DESC)
    @Query(value = """
           SELECT p
           FROM Posts p
           WHERE p.isDeleted = false
             AND p.user.id IN :userIds
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
             AND (:start IS NULL OR p.createdAt >= :start)
             AND (:end   IS NULL OR p.createdAt <  :end)
           """)
    Page<Posts> findFeedByUserIdsAndDateRangeOrderByLikeCountDesc(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
