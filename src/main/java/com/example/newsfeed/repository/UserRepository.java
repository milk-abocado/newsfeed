package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByIdAndDeletedFalse(Long id);

    class ProfileUpdateHistoryRepository {
    }
}
