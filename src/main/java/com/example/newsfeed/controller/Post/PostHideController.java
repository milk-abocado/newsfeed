package com.example.newsfeed.controller.Post;

import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.service.Post.PostHideService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        // 1. 로그인 여부 확인
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) return ResponseEntity.unprocessableEntity().body("로그인이 필요합니다.");

        try {
            // 2. 숨김 처리 서비스 호출
            return ResponseEntity.ok(postHideService.hidePost(loginUser.getId(), postId));
        } catch (ResponseStatusException e) {
            // 3. 서비스에서 던진 상태 코드/메시지를 그대로 전달
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); // ← 메시지 보장
        }
    }
}
