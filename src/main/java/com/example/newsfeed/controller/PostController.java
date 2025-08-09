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

        // 게시물 생성 후 응답 반환
        PostResponseDto responseDto = postService.createPost(requestDto, loggedInUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 게시물 단건 조회 API
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId, HttpSession session) {
        try {
            Users loggedInUser = (Users) session.getAttribute("user");

            // 게시물 ID로 조회
            PostResponseDto responseDto = postService.getPostById(postId, loggedInUser);
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            // 예외 메시지 앞부분에 따라 HTTP 상태 코드 분기
            String msg = e.getMessage();
            if (msg.startsWith("401:")) {
                return ResponseEntity.status(401).body(msg.substring(4));
            } else if (msg.startsWith("404:")) {
                return ResponseEntity.status(404).body(msg.substring(4));
            } else {
                return ResponseEntity.status(400).body(msg); // 기타 예외
            }
        }
    }

    // 게시물 전체 조회 API (페이징 지원)
    @GetMapping
    public ResponseEntity<PostPageResponseDto> getAllPosts(
            @RequestParam(defaultValue = "0") int page,  // 기본값 0 (첫 페이지)
            @RequestParam(defaultValue = "10") int size,  // 기본값 10개씩
            HttpSession session
    ) {
        Users loggedInUser = (Users) session.getAttribute("user");

        // 전체 게시물 조회 및 페이징 처리
        PostPageResponseDto response = postService.getAllPosts(page, size, loggedInUser);
        return ResponseEntity.ok(response);
    }

}
