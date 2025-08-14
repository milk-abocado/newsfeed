package com.example.newsfeed.controller.Like;

import com.example.newsfeed.dto.Like.LikeStatusResponseDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.service.Like.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LikeController {

    private final LikeService likeService;

    // ===== 게시물 좋아요/취소 =====

    // 게시물 좋아요
    // @param postId  좋아요를 누를 게시물 ID
    // @param session 현재 HTTP 세션 (로그인 사용자 정보 포함)
    // @return 좋아요 상태 및 개수를 담은 DTO 또는 에러 메시지
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> likePost(@PathVariable Long postId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            LikeStatusResponseDto res = likeService.likePost(postId, loginUser.getId());
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 게시물 좋아요 취소
    // @param postId  좋아요를 취소할 게시물 ID
    // @param session 현재 HTTP 세션 (로그인 사용자 정보 포함)
    // @return 좋아요 취소 상태 및 개수를 담은 DTO 또는 에러 메시지
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            LikeStatusResponseDto res = likeService.unlikePost(postId, loginUser.getId());
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // ===== 댓글 좋아요/취소 =====

    // 댓글 좋아요
    // @param commentId 좋아요를 누를 댓글 ID
    // @param session   현재 HTTP 세션 (로그인 사용자 정보 포함)
    // @return 좋아요 상태 및 개수를 담은 DTO 또는 에러 메시지
    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            LikeStatusResponseDto res = likeService.likeComment(commentId, loginUser.getId());
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 댓글 좋아요 취소
    // @param commentId 좋아요를 취소할 댓글 ID
    // @param session   현재 HTTP 세션 (로그인 사용자 정보 포함)
    // @return 좋아요 취소 상태 및 개수를 담은 DTO 또는 에러 메시지
    @DeleteMapping("/comments/{commentId}/likes")
    public ResponseEntity<?> unlikeComment(@PathVariable Long commentId, HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            LikeStatusResponseDto res = likeService.unlikeComment(commentId, loginUser.getId());
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
