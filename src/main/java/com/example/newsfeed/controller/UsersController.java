package com.example.newsfeed.controller;

import com.example.newsfeed.repository.UsersRepository;
import com.example.newsfeed.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteCurrentUser(Authentication authentication) {
        String username = authentication.getName();  // 인증된 사용자명
        usersService.softDeleteUser(username);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
