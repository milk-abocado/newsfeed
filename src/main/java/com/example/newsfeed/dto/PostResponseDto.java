package com.example.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private Long id;                        // 게시물 고유 ID
    private Long userId;                    // 게시물 작성 유저 ID
    private String nickname;                // 작성자 닉네임
    private String content;                 // 게시물 내용

    @JsonProperty("imageUrls")              // 반환될 키 이름을 "imageUrls"로 설정
    private List<String> imageUrlList;      // 게시물에 첨부된 이미지 URL 리스트

    private LocalDateTime createdAt;        // 게시물 생성 시각
    private LocalDateTime updatedAt;        // 게시물 수정 시각

    private long likeCount;                 // 좋아요 개수
    private boolean likedByCurrentUser;     // 현재 로그인 사용자가 좋아요 눌렀는지 여부
}
