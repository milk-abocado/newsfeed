package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.service.PostFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostFeedController {

    private final PostFeedService postFeedService;

    // 뉴스피드 조회 API
    @GetMapping("/following")
    public PostFeedResponseDto getNewsfeed(
            @RequestParam Long userId, // 임시: 로그인 사용자 ID
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return postFeedService.getNewsfeed(userId, pageable);
    }
}
