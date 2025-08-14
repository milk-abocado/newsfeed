package com.example.newsfeed.repository.User;

import com.example.newsfeed.entity.User.ProfileUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileUpdateHistoryRepository extends JpaRepository<ProfileUpdateHistory, Long> {
}