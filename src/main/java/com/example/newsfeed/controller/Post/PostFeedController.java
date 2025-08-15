package com.example.newsfeed.controller.Post;

import com.example.newsfeed.dto.Post.PostFeedResponseDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.service.Post.PostFeedService;
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
    // 1. 수정일 최신순(기본): GET http://localhost:8080/posts/following?sort=updated&page=0&size=10
    //                      기간 포함(작성일 기준, 양식: yyyy-MM-dd 또는 yyyy.MM.dd)
    //                    : GET http://localhost:8080/posts/following?sort=updated&start=2024-05-01&end=2024-05-27&page=0&size=10
    // 2. 좋아요 많은 순    : GET http://localhost:8080/posts/following?sort=likes&page=0&size=10
    //                      기간 포함
    //                    : GET http://localhost:8080/posts/following?sort=likes&start=2024.05.01&end=2024.05.27&page=0&size=10
    @GetMapping("/following")
    public ResponseEntity<?> getNewsfeed(
            @RequestParam(defaultValue = "updated") String sort,  // updated | likes
            @RequestParam(required = false) String start,         // yyyy-MM-dd 또는 yyyy.MM.dd
            @RequestParam(required = false) String end,           // yyyy-MM-dd 또는 yyyy.MM.dd
            @PageableDefault() Pageable pageable,
            @SessionAttribute(value = "user", required = false) Users loginUser
    ) {
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        try {
            PostFeedResponseDto res = postFeedService.getNewsfeed(loginUser.getId(), pageable, sort, start, end);
            return ResponseEntity.ok(res);

        } catch (ResponseStatusException ex) {
            // 서비스에서 던진 명시적 예외 (400/403/404 등)
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());

        } catch (Exception e) {
            // 알 수 없는 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }
}
