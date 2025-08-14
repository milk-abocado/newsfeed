package com.example.newsfeed.dto.User;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileUpdateRequestDto {
    private String nickname;
    private String bio;
    private String profileImage;
    private String hometown;
    private String school;
}
