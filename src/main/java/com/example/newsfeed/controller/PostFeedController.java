package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostFeedService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostFeedController {

    private final PostFeedService postFeedService;

    // 뉴스피드 조회 API
    @GetMapping("/following")
    public PostFeedResponseDto getNewsfeed(
            @PageableDefault(size = 10) Pageable pageable,
            @SessionAttribute(value = "user", required = false) Users loginUser
    ) {

        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long userId = loginUser.getId();
        return postFeedService.getNewsfeed(userId, pageable);
    }
}