package com.example.newsfeed.controller;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.service.UserService;
import jakarta.servlet.http.HttpSession;
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

    // 프로필 수정
    @PatchMapping("/{userID}")
    public String updateUserProfile(@RequestBody UserProfileUpdateRequestDto dto,
                                    HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");

        if (loginUser == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        userService.updateUserProfile(loginUser.getId(), dto);
        return "프로필 수정이 완료 되었습니다.";
    }

    // 비밀번호 변경
    @PatchMapping("/{usersID}/change")
    public String changePassword(@PathVariable("usersID") Long usersID,
                                 HttpSession session,
                                 @RequestBody ChangePasswordRequestDto dto) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }

        userService.changePassword(user.getId(), dto);
        return "비밀번호가 변경되었습니다.";
    }
}
