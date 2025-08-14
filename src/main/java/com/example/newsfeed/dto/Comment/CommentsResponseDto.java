package com.example.newsfeed.dto.Comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsResponseDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;             // 작성자 닉네임
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long likeCount;              // 좋아요 개수
    private boolean likedByCurrentUser;  // 현재 로그인 사용자가 좋아요 눌렀는지
}
