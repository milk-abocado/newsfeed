package com.example.newsfeed.controller;

import com.example.newsfeed.dto.EmailRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.dto.UsersRequestDto;
import com.example.newsfeed.service.EmailService;
import com.example.newsfeed.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final EmailService emailService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UsersRequestDto request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            return new ResponseEntity<>("이메일 인증이 필요합니다.", HttpStatus.FORBIDDEN);
        }
        try {
            Users user = usersService.signup(request);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // 이메일 인증 메일
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody EmailRequestDto request) {
        try {
            String token = emailService.createToken(request.getEmail());
            String verifyUrl = "http://localhost:8080/auth/verify?token=" + token;
            return ResponseEntity.ok(Map.of(
                    "message", "인증 메일이 발송되었습니다.",
                    "verificationUrl", verifyUrl
            ));
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 인증 링크
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean success = emailService.verifyEmail(token);
        if (success) {
            return new ResponseEntity<>("인증 완료", HttpStatus.OK);
        }
        return new ResponseEntity<>("인증 실패", HttpStatus.BAD_REQUEST);
    }
}
