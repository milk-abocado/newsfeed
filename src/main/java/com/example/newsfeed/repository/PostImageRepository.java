package com.example.newsfeed.repository;

import com.example.newsfeed.entity.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImages, Long> {
}
