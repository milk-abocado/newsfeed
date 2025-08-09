package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Users, String>{
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
