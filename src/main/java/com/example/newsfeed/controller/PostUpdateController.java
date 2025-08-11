package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostFeedItemDto;
import com.example.newsfeed.dto.PostUpdateRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostUpdateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        if (loginUser == null) {
            // 401로 문자열만 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            PostFeedItemDto dto = postUpdateService.updatePost(postId, requestDto, loginUser.getId());
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            // Service에서 던진 상태코드 + reason만 문자열로 바디에 담아 반환
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }

//        Users loginUser = (Users) session.getAttribute("user");
//        // 로그인한 상태인지 확인
//        if (loginUser == null) {
//            return ResponseEntity.unprocessableEntity().body("로그인이 필요합니다.");
//        }
//        Long userId = loginUser.getId();
//
//        PostFeedItemDto dto = postUpdateService.updatePost(postId, requestDto, userId);
//        return ResponseEntity.ok(dto);
    }
}
