package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostUpdateRequestDto;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.service.PostUpdateService;
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
            @RequestBody PostUpdateRequestDto requestDto
    ) {
        // 현재 로그인한 사용자 ID 추출 (임시 코드)
        Long userId = 1L; // 추후 로그인 정보로 대체 예정

        Posts updatedPost = postUpdateService.updatePost(postId, requestDto, userId);
        return ResponseEntity.ok().body(updatedPost);
    }
}
