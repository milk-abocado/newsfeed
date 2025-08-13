package com.example.newsfeed.controller;

import com.example.newsfeed.dto.PostPageResponseDto;
import com.example.newsfeed.dto.PostRequestDto;
import com.example.newsfeed.dto.PostResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController                 // REST API 컨트롤러임을 명시
@RequiredArgsConstructor        // 생성자 주입을 위한 Lombok 어노테이션
@RequestMapping("/posts")    // 이 컨트롤러의 기본 URL 경로
public class PostController {

    private final PostService postService;

    // 게시물 생성 API
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto requestDto, HttpSession session) {

        // 필수 조건: content, 이미지, 삭제 이미지 ID 중 하나는 반드시 있어야 함
        if (requestDto.isAllEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("content, addImageUrl, deleteImageId 중 하나는 반드시 포함되어야 합니다.");
        }

        // 이미지가 3장을 초과하면 예외 반환
        if (requestDto.getImageUrlList() != null && requestDto.getImageUrlList().size() > 3) {
            return ResponseEntity
                    .badRequest()
                    .body("이미지는 최대 3장까지만 업로드할 수 있습니다.");
        }

        Users loggedInUser = (Users) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("로그인하지 않은 사용자입니다.");
        }

        try {
            PostResponseDto res = postService.createPost(requestDto, loggedInUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(res); // 정상은 JSON
        } catch (ResponseStatusException ex) {
            // 서비스에서 ResponseStatusException 던진 경우 -> 문자열만
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (IllegalArgumentException ex) {
            // 혹시 기존 "401: ..." 패턴을 쓰는 코드가 남아있다면 안전망
            String msg = ex.getMessage();
            if (msg != null && msg.startsWith("401:")) {
                return ResponseEntity.status(401).body(msg.substring(4));
            } else if (msg != null && msg.startsWith("404:")) {
                return ResponseEntity.status(404).body(msg.substring(4));
            }
            return ResponseEntity.status(400).body(msg != null ? msg : "잘못된 요청입니다.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 게시물 단건 조회 API
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId, HttpSession session) {
        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인하지 않은 사용자입니다");
        }

        try {
            PostResponseDto res = postService.getPostById(postId, loggedInUser);
            return ResponseEntity.ok(res); // 정상은 JSON
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason()); // 문자열만
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.startsWith("404:")) {
                return ResponseEntity.status(404).body(msg.substring(4));
            }
            if (msg != null && msg.startsWith("401:")) {
                return ResponseEntity.status(401).body(msg.substring(4));
            }
            return ResponseEntity.status(400).body(msg != null ? msg : "잘못된 요청입니다.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 게시물 전체 조회 API (페이징 지원)
    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인하지 않은 사용자입니다.");
        }

        try {
            PostPageResponseDto res = postService.getAllPosts(page, size, user);
            return ResponseEntity.ok(res); // 정상은 JSON
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }
}
