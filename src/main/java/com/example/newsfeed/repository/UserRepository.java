package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByIdAndIsDeletedFalse(Long id);

    Optional<Users> findByEmailAndIsDeletedFalse(String email);
}