package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {

    // 삭제되지 않은 게시물만 단건 조회
    Optional<Posts> findByIdAndIsDeletedFalse(Long id);

    // 삭제되지 않은 게시물만 전체 조회
    List<Posts> findAllByIsDeletedFalse();
}