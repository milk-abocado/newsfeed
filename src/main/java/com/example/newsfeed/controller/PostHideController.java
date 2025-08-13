package com.example.newsfeed.controller;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostHideService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * 게시물 차단 API
 * - POST /posts/{postId}/hide
 * - 성공 시 200
 * - 실패시  ResponseStatusException(404) -> PostHideService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostHideController {

    private final PostHideService postHideService;

    @PostMapping("/{postId}/hide")
    public ResponseEntity<?> hide(@PathVariable Long postId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) return ResponseEntity.unprocessableEntity().body("로그인이 필요합니다.");
        try {
            return ResponseEntity.ok(postHideService.hidePost(loginUser.getId(), postId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); // ← 메시지 보장
        }
    }
}
