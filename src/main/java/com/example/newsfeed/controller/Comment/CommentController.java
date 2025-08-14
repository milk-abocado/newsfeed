package com.example.newsfeed.controller.Comment;

import com.example.newsfeed.dto.Comment.CommentsPageResponseDto;
import com.example.newsfeed.dto.Comment.CommentsRequestDto;
import com.example.newsfeed.dto.Comment.CommentsResponseDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.service.Comment.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성: POST /posts/{postId}/comments
    // @param postId   댓글을 달 게시물 ID
    // @param session  현재 요청의 세션 (로그인 사용자 확인)
    // @param req      댓글 내용 DTO (content 필수)
    // @return 201 Created + 생성된 댓글 DTO
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> create(@PathVariable Long postId,
                                    HttpSession session,
                                    @RequestBody CommentsRequestDto req) {

        // 로그인 여부 확인 (세션에 Users 저장 가정)
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            // 401 Unauthorized: 로그인 필요
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            // 서비스에 위임하여 댓글 생성
            CommentsResponseDto res = commentService.create(postId, loginUser.getId(), req);
            // 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (ResponseStatusException ex) {
            // 서비스에서 던진 상태코드/사유를 그대로 전달
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            // 예상치 못한 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 댓글 조회(페이지네이션): GET /posts/{postId}/comments?page=0&size=10
    // @param postId 게시물 ID
    // @param page   페이지 번호(0부터 시작)
    // @param size   페이지 크기(기본 10)
    // @return 200 OK + 댓글 페이지 응답 DTO
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> list(@PathVariable Long postId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            // 401 Unauthorized: 로그인 필요
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            Long currentUserId = loginUser.getId();

            CommentsPageResponseDto res = commentService.getList(postId, page, size, currentUserId);
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }


    // 댓글 수정: PATCH /comments/{commentId}
    // @param commentId 수정할 댓글 ID
    // @param session   로그인 사용자 세션
    // @param req       수정할 내용 DTO (content만 허용)
    // @return 200 OK + 수정된 댓글 DTO
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> update(@PathVariable Long commentId,
                                    HttpSession session,
                                    @Valid @RequestBody CommentsRequestDto req) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            // Service 에서 "댓글 작성자만" 수정 가능하도록 권한 체크
            CommentsResponseDto res = commentService.update(commentId, loginUser.getId(), req);
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            // 400(내용 비어 있음), 403(권한 없음), 404(댓글 없음) 등
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 댓글 삭제: DELETE /comments/{commentId}
    // @param commentId 삭제할 댓글 ID
    // @param session   로그인 사용자 세션
    // @return 200 OK + 성공 메시지
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Long commentId,
                                    HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            // Service 에서 권한 체크 후 삭제 수행
            commentService.delete(commentId, loginUser.getId());
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (ResponseStatusException ex) {
            // 403(권한 없음), 404(댓글 없음) 등
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
