package com.example.newsfeed.controller;

import com.example.newsfeed.service.PostDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostDeleteController {

    private final PostDeleteService postDeleteService;

    // 게시물 삭제 API: DELETE /posts/{postId}
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId  // 임시로 로그인 유저 ID를 받아옴 -> 추후 로그인 기능과 merge시 변경 예정
    ) {
        postDeleteService.deletePost(postId, userId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }
}
