package com.example.newsfeed.dto.User;

import com.example.newsfeed.dto.Follow.FollowListDto;
import com.example.newsfeed.entity.User.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private int knowFollower;
    private List<FollowListDto> knowFollowers;

    public UserProfileResponseDto(Users user, int knowFollower, List<FollowListDto> knowFollowers) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.profileImage = user.getProfileImage();
        this.hometown = user.getHometown();
        this.school = user.getSchool();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.knowFollower = knowFollower;
        this.knowFollowers = knowFollowers;
    }
}