package com.example.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockResponseDto {
    private Long userId;     // 차단당한 사용자 ID
    private String nickname; // 차단당한 사용자 닉네임
}
