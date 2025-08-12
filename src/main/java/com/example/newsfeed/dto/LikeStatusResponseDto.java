package com.example.newsfeed.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeStatusResponseDto {
    private Long targetId;      // postId or commentId
    private boolean liked;      // true(좋아요 상태) / false(좋아요 취소 상태)
    private long likeCount;     // 현재 총 좋아요 개수
    private String targetType;  // "POST" or "COMMENT"
}
