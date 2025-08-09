package com.example.newsfeed.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginRequestDto {
    private String email;
    private String password;
}
