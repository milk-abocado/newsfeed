package com.example.newsfeed.controller;

import com.example.newsfeed.dto.EmailRequestDto;
import com.example.newsfeed.dto.AuthLoginRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.dto.AuthRequestDto;
import com.example.newsfeed.service.EmailService;
import com.example.newsfeed.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequestDto request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            return new ResponseEntity<>("이메일 인증이 필요합니다.", HttpStatus.FORBIDDEN);
        }
        try {
            Users user = authService.signup(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED); // 상태 코드 수정
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto request, HttpSession session) {
        try {
            Users user = authService.login(request.getEmail(), request.getPassword());

            // 세션에 로그인 사용자 정보 저장
            session.setAttribute("user", user);

            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("로그인 하지 않았습니다.");
        }

        Users user = (Users) userObj;  // 캐스팅
        Long userId = user.getId();
        String email = user.getEmail();

        session.invalidate();
        return ResponseEntity.ok("User " + userId + " (" + email + "): 로그아웃 완료");
    }

}
