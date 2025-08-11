package com.example.newsfeed.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequestDto {
    private String content;                   // 수정할 게시물 내용
    private List<String> addImageUrl;         // 새로 추가할 이미지 URL 목록
    private List<Long> deleteImageId;         // 삭제할 이미지 ID 목록
}
