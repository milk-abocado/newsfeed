package com.example.newsfeed.controller;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.dto.FollowResponseMessageDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.FollowerBlockService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
public class FollowerBlockController {
    private final FollowerBlockService followerBlockService;

    //차단
    @PostMapping("/block")
    public ResponseEntity<?> blockUser(
            @RequestBody BlockRequestDto requestDto,
            HttpSession session) {

        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FollowResponseMessageDto("로그인이 필요합니다."));
        }
        Long userId = loginUser.getId(); //로그인 유저 ID
        String message = followerBlockService.blockUser(userId, requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    //차단 해제
    @PostMapping("/unblock")
    public ResponseEntity<?> unblockUser(
            @RequestBody BlockRequestDto requestDto,
            HttpSession session) {

        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FollowResponseMessageDto("로그인이 필요합니다."));
        }
        Long userId = loginUser.getId(); //로그인 유저 ID
        String message = String.valueOf(followerBlockService.unblockUser(userId, requestDto));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    //차단 목록 조회
    @GetMapping("/blocked")
    public ResponseEntity<?> getBlockedUsers(HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FollowResponseMessageDto("로그인이 필요합니다."));
        }
        Long userId = loginUser.getId(); //로그인 유저 ID
        return ResponseEntity.ok(followerBlockService.getBlockedUsers(userId));
    }

    //특정 사용자 차단 여부 확인
    @GetMapping("/blocked/{targetUserId}")
    public ResponseEntity<?> isBlocked(
            @PathVariable Long targetUserId,
            HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FollowResponseMessageDto("로그인이 필요합니다."));
        }
        Long userId = loginUser.getId(); //로그인 유저 ID
        boolean blocked = followerBlockService.isBlocked(userId, targetUserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("blocked", blocked);
        return ResponseEntity.ok(response);
    }

    public FollowerBlockService getService() {
        return followerBlockService;
    }
}
