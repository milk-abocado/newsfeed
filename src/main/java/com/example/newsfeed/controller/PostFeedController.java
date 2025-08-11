package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostFeedService;
import jakarta.servlet.http.HttpSession;
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
            @PageableDefault(size = 10) Pageable pageable,
            HttpSession session
    ) {

        Users loginUser = (Users) session.getAttribute("user");
        // 로그인한 상태인지 확인
        if (loginUser == null) {
            System.out.println("로그인 필요");
        }
        Long userId = loginUser.getId();
        return postFeedService.getNewsfeed(userId, pageable);
    }
}