package com.example.newsfeed.repository.User;

import com.example.newsfeed.entity.User.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByIdAndIsDeletedFalse(Long id);

    Optional<Users> findByEmailAndIsDeletedFalse(String email);
}