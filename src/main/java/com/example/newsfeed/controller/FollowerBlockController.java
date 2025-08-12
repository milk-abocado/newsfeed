package com.example.newsfeed.controller;

import com.example.newsfeed.dto.BlockRequestDto;
import com.example.newsfeed.entity.BlockedUser;
import com.example.newsfeed.service.FollowerBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
public class FollowerBlockController {
    private final FollowerBlockService followerBlockService;

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
        String message = followerBlockService.unblockUser(userId, requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    //차단 목록 조회
    @GetMapping("/blocked")
    public ResponseEntity<List<BlockedUser>> getBlockedUsers(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(followerBlockService.getBlockedUsers(userId));
    }

    //특정 사용자 차단 여부 확인
    @GetMapping("/blocked/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> isBlocked(
            @PathVariable Long targetUserId,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        boolean blocked = followerBlockService.isBlocked(userId, targetUserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("blocked", blocked);
        return ResponseEntity.ok(response);
    }
}
