package com.example.newsfeed.dto;

import com.example.newsfeed.entity.PostImages;
import com.example.newsfeed.entity.Posts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Builder

//각 게시물의 단일정보 DTO
public class PostFeedItemDto {

    private Long id;                    // 게시물 ID
    private Long userId;                // 게시물 작성자 ID
    private String nickname;            // 작성자 닉네임
    private String content;             // 게시물 내용

    private List<String> imageUrls;     // 게시물 이미지 URL 리스트
    private LocalDateTime createdAt;    // 게시물 생성 시간
    private LocalDateTime updatedAt;    // 게시물 수정 시간

    private long likeCount;             // 좋아요 개수
    private boolean likedByCurrentUser; // 현재 로그인 사용자가 좋아요 눌렀는지

    // 좋아요 정보까지 포함해 매핑
    public static PostFeedItemDto fromEntity(Posts p, long likeCount, boolean likedByCurrentUser) {
        return PostFeedItemDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .nickname(p.getUser().getNickname())
                .content(p.getContent())
                .imageUrls(p.getPostImageList().stream()
                        .map(PostImages::getImageUrl)
                        .collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .likeCount(likeCount)
                .likedByCurrentUser(likedByCurrentUser)
                .build();
    }

//    // Entity → DTO 변환 메서드
//    public static PostFeedItemDto of(
//            Long id, Long userId, String nickname, String content,
//            List<String> imageUrlList, LocalDateTime createdAt, LocalDateTime updatedAt,
//            long likeCount, boolean likedByCurrentUser
//    ) {
//        return PostFeedItemDto.builder()
//                .id(id)
//                .userId(userId)
//                .nickname(nickname)
//                .content(content)
//                .imageUrls(imageUrlList)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .likeCount(likeCount)
//                .likedByCurrentUser(likedByCurrentUser)
//                .build();
//    }
}