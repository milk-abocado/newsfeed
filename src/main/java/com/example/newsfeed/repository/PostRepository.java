package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {

    // 삭제되지 않은 게시물만 단건 조회
    Optional<Posts> findByIdAndIsDeletedFalse(Long id);

    // 삭제되지 않은 게시물만 전체 조회
    List<Posts> findAllByIsDeletedFalse();


    /**
     * 뉴스피드 게시물 조회용 쿼리
     * - 삭제되지 않은 게시물
     * - 본인 + 팔로우한 사용자 한해서 조회
     * - 최신순 정렬 (내림차순)
     * - 페이징 처리
     */

    @Query("SELECT p FROM Posts p " +
            "WHERE p.isDeleted = false AND p.user.id IN :userIds " +
            "ORDER BY p.createdAt DESC") // 내림차순 정렬
    Page<Posts> findFeedByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);
}
