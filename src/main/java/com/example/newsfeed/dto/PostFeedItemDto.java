package com.example.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor

//각 게시물의 단일정보 DTO
public class PostFeedItemDto {

    private Long id;                     // 게시물 ID
    private Long userId;                 // 게시물 작성자 ID
    private String nickname;            // 작성자 닉네임
    private String content;             // 게시물 내용
    private List<String> imageUrls;     // 게시물 이미지 URL 리스트
    private LocalDateTime createdAt;    // 게시물 생성 시간
}