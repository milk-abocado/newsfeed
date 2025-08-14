package com.example.newsfeed.controller;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.dto.BlockResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.FollowerBlockService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
public class FollowerBlockController {

    private final FollowerBlockService followerBlockService;

    // ===== 차단 =====
    @PostMapping("/block")
    public ResponseEntity<?> blockUser(
            @Valid @RequestBody BlockRequestDto requestDto,
            HttpSession session) {

        try {
            // 세션에서 로그인 유저 정보 가져오기
            Users loginUser = (Users) session.getAttribute("user");
            if (loginUser == null) {
                // 로그인 안 한 경우 401 Unauthorized 응답
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 자기 자신 차단 불가
            if (loginUser.getId().equals(requestDto.getTargetUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("자기 자신은 차단할 수 없습니다.");
            }

            // 서비스 계층 호출하여 차단 처리
            String message = followerBlockService.blockUser(loginUser.getId(), requestDto);
            // 차단 성공 시 201 Created 응답
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (IllegalStateException e) {
            // 이미 차단한 경우 등 상태 충돌 시 409 오류
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외는 500 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    // ===== 차단 해제 =====
    @PostMapping("/unblock")
    public ResponseEntity<?> unblockUser(
            @Valid @RequestBody BlockRequestDto requestDto,
            HttpSession session) {

        try {
            // 세션에서 로그인 유저 정보 가져오기
            Users loginUser = (Users) session.getAttribute("user");
            if (loginUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 자기 자신 차단/해제 불가
            if (loginUser.getId().equals(requestDto.getTargetUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("자기 자신에 대한 차단/해제는 허용되지 않습니다.");
            }

            // 차단 해제 처리
            String message = followerBlockService.unblockUser(loginUser.getId(), requestDto);
            return ResponseEntity.ok(message);

        } catch (IllegalStateException e) {
            // 차단 상태가 아닌데 해제하려는 경우 등
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    // ===== 차단 목록 조회 =====
    @GetMapping("/blocked")
    public ResponseEntity<?> getBlockedUsers(HttpSession session) {
        try {
            Users loginUser = (Users) session.getAttribute("user");

            if (loginUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 로그인한 사용자가 차단한 사용자 목록 조회
            List<BlockResponseDto> blockedUsers = followerBlockService.getBlockedUsers(loginUser.getId());

            // 차단한 사용자가 없으면 안내 메시지 반환
            if (blockedUsers.isEmpty()) {
                return ResponseEntity.ok("차단한 사용자가 없습니다.");
            }

            // 차단 목록 반환
            return ResponseEntity.ok(blockedUsers);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    // ===== 특정 사용자 차단 여부 확인 =====
    @GetMapping("/blocked/{targetUserId}")
    public ResponseEntity<?> isBlocked(
            @PathVariable Long targetUserId,
            HttpSession session) {

        try {
            Users loginUser = (Users) session.getAttribute("user");
            if (loginUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 자기 자신은 차단 여부 체크 대상에서 제외
            if (loginUser.getId().equals(targetUserId)) {
                return ResponseEntity.ok(
                        Map.of("blocked", false, "message", "자기 자신은 차단 상태로 표시되지 않습니다.")
                );
            }

            // 서비스 호출하여 차단 여부 확인
            boolean blocked = followerBlockService.isBlocked(loginUser.getId(), targetUserId);

            // 차단 여부에 따른 메시지 구성
            String message = blocked
                    ? "해당 사용자는 차단 상태입니다."
                    : "해당 사용자는 차단 상태가 아닙니다.";

            // boolean 값과 설명 메시지를 함께 반환
            return ResponseEntity.ok(
                    Map.of("blocked", blocked, "message", message)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }
}
