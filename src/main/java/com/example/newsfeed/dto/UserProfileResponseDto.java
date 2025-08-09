package com.example.newsfeed.dto;

import com.example.newsfeed.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String bio;
    private String profileImage;
    private String hometown;
    private String school;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserProfileResponseDto(Users user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.profileImage = user.getProfileImage();
        this.hometown = user.getHometown();
        this.school = user.getSchool();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}