package com.example.newsfeed.repository;

import com.example.newsfeed.entity.ProfileUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileUpdateHistoryRepository extends JpaRepository<ProfileUpdateHistory, Long> {
}