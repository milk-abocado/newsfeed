package com.example.newsfeed.controller;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 프로필 수정 (헤더의 X-USER-ID 사용)
    @PatchMapping("/{userID}")
    public String updateUserProfile(@RequestBody UserProfileUpdateRequestDto dto,
                                    @RequestHeader("X-USER-ID") Long userId) {
        userService.updateUserProfile(userId, dto);
        return "프로필 수정이 완료 되었습니다.";
    }

    // 비밀번호 변경 (헤더의 X-USER-ID 사용)
    @PatchMapping("/{usersID}")
    public String changePassword(@RequestHeader("X-USER-ID") Long userId,
                                 @RequestBody ChangePasswordRequestDto dto) {
        userService.changePassword(userId, dto);
        return "비밀번호가 변경되었습니다.";
    }
}
