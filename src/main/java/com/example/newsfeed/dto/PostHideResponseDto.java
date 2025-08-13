package com.example.newsfeed.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시물 숨김 성공 응답 DTO
 * - postId: 숨김 대상 게시물 ID
 * - hidden: 항상 true (숨김 상태)
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHideResponseDto {
    private Long postId;
    private boolean hidden;
    private String hiddenAt;
}
