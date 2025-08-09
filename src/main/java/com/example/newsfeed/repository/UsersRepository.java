package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    // 사용자 조회 메서드 정의 (필요한 경우)
}
