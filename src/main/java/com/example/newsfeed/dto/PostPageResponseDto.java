package com.example.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponseDto {
    private List<PostResponseDto> contentList;  // 현재 페이지에 포함된 게시물 리스트
    private int page;                       // 현재 페이지 번호(0부터 시작)
    private int size;                       // 한 페이지에 포함된 게시물 수
    private long totalElements;             // 전체 게시물 개수
    private int totalPages;                 // 전체 페이지 수
}
