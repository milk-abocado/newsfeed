package com.example.newsfeed.repository;

import com.example.newsfeed.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 여부 확인
    boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

    // 특정 사용자가 특정 댓글에 누른 좋아요 삭제
    long deleteByComment_IdAndUser_Id(Long commentId, Long userId);

    // 특정 댓글의 전체 좋아요 개수 조회
    long countByComment_Id(Long commentId);
}
