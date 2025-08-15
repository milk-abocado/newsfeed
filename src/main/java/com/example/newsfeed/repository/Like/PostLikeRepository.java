package com.example.newsfeed.repository.Like;

import com.example.newsfeed.entity.Like.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 특정 사용자가 특정 게시물에 좋아요를 눌렀는지 여부를 확인
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    // 특정 사용자가 특정 게시물에 누른 좋아요 엔티티 조회
    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);

    // 특정 게시물의 전체 좋아요 개수 조회
    long countByPost_Id(Long postId);
}
