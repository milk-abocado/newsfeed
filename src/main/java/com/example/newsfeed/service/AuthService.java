package com.example.newsfeed.service;


import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.AuthRequestDto;
import com.example.newsfeed.dto.ResetPasswordRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // password 형식
    private static final Pattern password_Pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    // email 형식
    private static final Pattern email_Pattern = Pattern.compile("^[a-zA-Z0-9+_.-]+@[A-Za-z0-9.-]+$");


    // 회원가입
    @Transactional
    public Users signup(AuthRequestDto request) {
        if(!email_Pattern.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (!emailService.isEmailVerified(request.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        if(!password_Pattern.matcher(request.getPassword()).matches()) {
            throw new IllegalArgumentException("비밀번호는 영문 + 숫자 + 특수문자를 최소 1글자씩 포함하며, 8자 이상이어야 합니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Users user = new Users(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                request.getNickname(),
                request.getSecurityQuestion(),
                request.getSecurityAnswer(),
                request.getProfileImage()
        );

        return authRepository.save(user);
    }

    // 로그인
    public Users login(String email, String rawPassword) {
        Optional<Users> userEmail = authRepository.findByEmail(email);

        // 이메일 확인
        if (userEmail.isEmpty()) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }

        Users user = userEmail.get();

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // 비밀번호 찾기
    @Transactional
    public String resetPassword(ResetPasswordRequestDto request) {

        Users user = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 비밀번호 찾기용 질문 확인
        if (!user.getSecurityQuestion().equals(request.getSecurityQuestion())) {
            throw new IllegalArgumentException("질문이 일치하지 않습니다.");
        }

        // 비밀번호 찾기용 답 확인
        if (!user.getSecurityAnswer().equals(request.getSecurityAnswer())) {
            throw new IllegalArgumentException("질문이 일치하지 않습니다.");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        user.setPassword(encodedPassword);

        System.out.println("임시 비밀번호: " + tempPassword);

        return tempPassword;
    }

}
