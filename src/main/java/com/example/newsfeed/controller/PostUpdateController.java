package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedItemDto;
import com.example.newsfeed.dto.PostUpdateRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostUpdateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostUpdateController {

    private final PostUpdateService postUpdateService;

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDto requestDto,
            HttpSession session
    ) {

        Users loginUser = (Users) session.getAttribute("user");
        // 로그인한 상태인지 확인
        if (loginUser == null) {
            return ResponseEntity.unprocessableEntity().body("로그인이 필요합니다.");
        }
        Long userId = loginUser.getId();

        PostFeedItemDto dto = postUpdateService.updatePost(postId, requestDto, userId);
        return ResponseEntity.ok(dto);
    }
}
