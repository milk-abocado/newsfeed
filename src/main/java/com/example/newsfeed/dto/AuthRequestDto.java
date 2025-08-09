package com.example.newsfeed.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String securityQuestion;
    private String securityAnswer;
    private String profileImage;
}
