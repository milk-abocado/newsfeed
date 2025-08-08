package com.example.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;                        // 게시물 고유 ID
    private Long userId;                    // 게시물 작성 유저 ID
    private String nickname;                // 작성자 닉네임
    private String content;                 // 게시물 내용

    @JsonProperty("imageUrls")              // 반환될 키 이름을 "imageUrls"로 설정
    private List<String> imageUrlList;      // 게시물에 첨부된 이미지 URL 리스트

    private LocalDateTime createdAt;        // 게시물 생성 시각
    private LocalDateTime updatedAt;        // 게시물 수정 시각

    // 생성자
    public PostResponseDto(Long id, Long userId, String nickname, String content,
                           List<String> imageUrlList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.imageUrlList = imageUrlList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
