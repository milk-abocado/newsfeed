package com.example.newsfeed.service;


import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.AuthRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (!password_Pattern.matcher(request.getPassword()).matches()) {
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

}
