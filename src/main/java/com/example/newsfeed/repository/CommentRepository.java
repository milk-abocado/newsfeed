package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    Page<Comments> findByPost_IdOrderByCreatedAtDesc(Long postId, Pageable pageable);
}
