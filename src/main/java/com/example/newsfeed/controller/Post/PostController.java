package com.example.newsfeed.controller.Post;

import com.example.newsfeed.dto.Post.*;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.service.Post.PostDeleteService;
import com.example.newsfeed.service.Post.PostService;
import com.example.newsfeed.service.Post.PostUpdateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostUpdateService postUpdateService;
    private final PostDeleteService postDeleteService;

    private ResponseEntity<String> str(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(msg);
    }

    // ===== 게시물 생성 =====
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto requestDto, HttpSession session) {
        Users loggedInUser = (Users) session.getAttribute("user");
        if (loggedInUser == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (requestDto == null) {
            return str(HttpStatus.BAD_REQUEST, "요청 데이터가 없습니다.");
        }
        if (requestDto.isAllEmpty()) {
            return str(HttpStatus.BAD_REQUEST, "content, addImageUrl, deleteImageId 중 하나 이상은 반드시 포함되어야 합니다.");
        }
        if (requestDto.getImageUrlList() != null && requestDto.getImageUrlList().size() > 3) {
            return str(HttpStatus.BAD_REQUEST, "이미지는 최대 3장까지만 업로드할 수 있습니다.");
        }
        try {
            PostResponseDto res = postService.createPost(requestDto, loggedInUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception ex) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 게시물 단건 조회 =====
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId, HttpSession session) {
        Users loggedInUser = (Users) session.getAttribute("user");
        if (loggedInUser == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            PostResponseDto res = postService.getPostById(postId, loggedInUser);
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return str(HttpStatus.NOT_FOUND, "존재하지 않는 게시물입니다.");
            }
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        } catch (Exception ex) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 게시물 전체 조회 =====
    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            PostPageResponseDto res = postService.getAllPosts(page, size, user);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 게시물 수정 =====
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody PostUpdateRequestDto requestDto,
                                        HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (requestDto == null || requestDto.isAllEmpty()) {
            return str(HttpStatus.BAD_REQUEST, "content, addImageUrl, deleteImageId 중 하나 이상은 반드시 포함되어야 합니다.");
        }
        try {
            PostFeedItemDto dto = postUpdateService.updatePost(postId, requestDto, loginUser.getId());
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                return str(HttpStatus.FORBIDDEN, "본인이 작성하지 않은 게시물입니다.");
            } else if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return str(HttpStatus.NOT_FOUND, "게시물이 존재하지 않습니다.");
            }
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 게시물 삭제 =====
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            postDeleteService.deletePost(postId, loginUser.getId());
            return str(HttpStatus.OK, "게시물이 삭제되었습니다.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                return str(HttpStatus.FORBIDDEN, "본인이 작성하지 않은 게시물입니다.");
            } else if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return str(HttpStatus.NOT_FOUND, "게시물이 존재하지 않습니다.");
            }
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }
}
