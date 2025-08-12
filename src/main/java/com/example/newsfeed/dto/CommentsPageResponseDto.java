package com.example.newsfeed.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentsPageResponseDto {
    private List<CommentsResponseDto> contentList;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
