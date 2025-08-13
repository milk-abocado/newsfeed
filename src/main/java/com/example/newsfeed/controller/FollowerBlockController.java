package com.example.newsfeed.controller;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.entity.UserBlock;
import com.example.newsfeed.service.FollowerBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
public class FollowerBlockController {
    private final FollowerBlockService followerBlockService;

    //공통: Principal에서 userId 안전 추출 필요
    private Long currentUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        String name = principal.getName();
        try {
            return Long.valueOf(name); // 이름이 숫자인 경우 (이미 userId가 들어있는 환경)
        } catch (NumberFormatException e) {
            return followerBlockService.getUserIdByUsername(name); // 이름이 username인 경우 DB로 매핑
        }    }

    //차단
    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockUser(
            @RequestBody BlockRequestDto requestDto,
            Principal principal) {

        Long userId = Long.valueOf(principal.getName()); //로그인 유저 ID
        String message = followerBlockService.blockUser(userId, requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    //차단 해제
    @PostMapping("/unblock")
    public ResponseEntity<Map<String, Object>> unblockUser(
            @RequestBody BlockRequestDto requestDto,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        String message = String.valueOf(followerBlockService.unblockUser(userId, requestDto));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    //차단 목록 조회
    @GetMapping("/blocked")
    public ResponseEntity<List<UserBlock>> getBlockedUsers(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(followerBlockService.getBlockedUsers(userId));
    }

    //특정 사용자 차단 여부 확인
    @GetMapping("/blocked/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> isBlocked(
            @PathVariable Long targetUserId,
            String principal) {
        Long userId = currentUserId(principal);
        boolean blocked = followerBlockService.isBlocked(userId, targetUserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("blocked", blocked);
        return ResponseEntity.ok(response);
    }

    public FollowerBlockService getService() {
        return followerBlockService;
    }
}
