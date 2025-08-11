package com.example.newsfeed.controller;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 프로필 조회
    @GetMapping("/{userId}")
    public UserProfileResponseDto getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    // 프로필 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUserProfile(@PathVariable Long userId,
                                    @RequestBody UserProfileUpdateRequestDto dto,
                                    HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 상태가 아닙니다.");
        }

        if (!loginUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자기 자신만 프로필을 수정할 수 있습니다.");
        }

        userService.updateUserProfile(loginUser.getId(), dto);
        return ResponseEntity.ok("프로필 수정이 완료 되었습니다.");
    }

    // 비밀번호 변경
    @PatchMapping("/{userId}/change")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId,
                                 HttpSession session,
                                 @RequestBody ChangePasswordRequestDto dto) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 상태가 아닙니다.");
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자기 자신만 비밀번호를 변경할 수 있습니다.");
        }

        try {
            userService.changePassword(user.getId(), dto);
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        }
        catch (ResponseStatusException ex) {
            // 예외 메시지와 상태코드를 그대로 전달
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String email, @RequestParam String password) {
        userService.deleteAccount(email, password);
        return "회원 탈퇴가 완료되었습니다.";
    }
}
