package com.example.newsfeed.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class CommentsResponseDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;   // 작성자 닉네임
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
