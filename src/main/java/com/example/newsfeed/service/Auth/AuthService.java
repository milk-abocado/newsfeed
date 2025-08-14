package com.example.newsfeed.service.Auth;

import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.Auth.AuthRequestDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.repository.Auth.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    // password 형식: 영문/숫자/특수문자 각 1+ 포함, 8자+
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    // email 형식
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // 회원가입
    @Transactional
    public Users signup(AuthRequestDto request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청 데이터가 없습니다.");
        }
        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        }
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
        if (request.getPassword() == null || !PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "비밀번호는 영문 + 숫자 + 특수문자를 최소 1글자씩 포함하며, 8자 이상이어야 합니다.");
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
        if (email == null || email.isBlank() || rawPassword == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 모두 입력해 주세요.");
        }

        Optional<Users> userOpt = authRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다.");
        }

        Users user = userOpt.get();

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "탈퇴한 사용자입니다.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
