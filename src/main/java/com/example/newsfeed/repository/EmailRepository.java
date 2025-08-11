package com.example.newsfeed.repository;

import com.example.newsfeed.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByEmail(String email);
    Optional<Email> findByVerificationToken(String verificationToken);
}
