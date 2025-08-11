package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostFeedController {

    private final PostFeedService postFeedService;

    // 뉴스피드 조회 API
    // 예시)
    // 1. 수정일 최신순(기본): GET http://localhost:8080/posts/following?sort=updated&page=0&size=10
    //                      기간 포함(작성일 기준, 양식: yyyy-MM-dd 또는 yyyy.MM.dd)
    //                    : GET http://localhost:8080/posts/following?sort=updated&start=2024-05-01&end=2024-05-27&page=0&size=10
    // 2. 좋아요 많은 순    : GET http://localhost:8080/posts/following?sort=likes&page=0&size=10
    //                      기간 포함
    //                    : GET http://localhost:8080/posts/following?sort=likes&start=2024.05.01&end=2024.05.27&page=0&size=10
    @GetMapping("/following")
    public PostFeedResponseDto getNewsfeed(
            @RequestParam(defaultValue = "updated") String sort,  // updated | likes
            @RequestParam(required = false) String start,         // 2024-05-01 or 2024.05.01
            @RequestParam(required = false) String end,           // 2024-05-27 or 2024.05.27
            @PageableDefault(size = 10) Pageable pageable,
            @SessionAttribute(value = "user", required = false) Users loginUser
    ) {
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return postFeedService.getNewsfeed(loginUser.getId(), pageable, sort, start, end);
    }
}
