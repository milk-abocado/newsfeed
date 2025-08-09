package com.example.newsfeed.repository;

import com.example.newsfeed.entity.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImages, Long> {
    // PostImages 엔티티에 대해 기본적인 CRUD 작업을 제공하는 인터페이스
    // - 기본적인 메소드: save(), findById(), findAll(), deleteById() 등
    // - PostImages 엔티티와 Long 타입의 ID를 사용하여 데이터베이스 작업을 수행
}
