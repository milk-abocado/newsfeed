package com.example.newsfeed.dto.Password;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}