package com.example.newsfeed.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    private String email;
    private String securityQuestion;
    private String securityAnswer;
}
