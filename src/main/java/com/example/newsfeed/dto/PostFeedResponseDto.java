package com.example.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

//뉴스피드 페이징 응답 전체 관리 DTO
public class PostFeedResponseDto {

    private List<PostFeedItemDto> content;  // 게시물 리스트 (한 페이지에 포함된)
    private int page;                       // 현재 페이지 번호
    private int size;                       // 페이지 당 게시물 수
    private long totalElements;            // 전체 게시물 수
    private int totalPages;                // 전체 페이지 수
}
