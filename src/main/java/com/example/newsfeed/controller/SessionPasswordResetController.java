package com.example.newsfeed.controller;

import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.PasswordResetResetRequestDto;
import com.example.newsfeed.dto.PasswordResetForgotRequestDto;
import com.example.newsfeed.dto.PasswordResetResetRequestDto;
import com.example.newsfeed.dto.PasswordResetVerifyRequestDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.UserRepository;
import com.example.newsfeed.service.MailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping("/auth/password/session")
@RequiredArgsConstructor
public class SessionPasswordResetController {

    private static final String KEY_EMAIL = "PR_EMAIL";
    private static final String KEY_CODE = "PR_CODE";
    private static final String KEY_EXPIRES_AT = "PR_EXPIRES_AT";
    private static final String KEY_VERIFIED = "PR_VERIFIED";
    private static final int EXPIRE_MINUTES = 10;

    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @PostMapping("/forgot")
    public ResponseEntity<String> forgot(@Valid @RequestBody PasswordResetForgotRequestDto req,
                                         HttpSession session) {
        String safeResponse = "비밀번호 재설정 안내가 전송되었다면 곧 도착합니다.";
        if (!StringUtils.hasText(req.getEmail())) return ResponseEntity.ok(safeResponse);

        Optional<Users> userOpt = userRepository.findByEmailAndIsDeletedFalse(req.getEmail());
        if (userOpt.isEmpty()) return ResponseEntity.ok(safeResponse);

        String code = generate6DigitCode();
        Instant expiresAt = Instant.now().plus(EXPIRE_MINUTES, ChronoUnit.MINUTES);

        session.setAttribute(KEY_EMAIL, req.getEmail());
        session.setAttribute(KEY_CODE, code);
        session.setAttribute(KEY_EXPIRES_AT, expiresAt);
        session.setAttribute(KEY_VERIFIED, Boolean.FALSE);

        String subject = "[Newsfeed] 비밀번호 재설정 인증코드";
        String body = """
                아래 인증코드를 입력해 비밀번호 재설정을 진행해 주세요. (유효시간 %d분)
                인증코드: %s

                본인이 요청하지 않았다면 이 메일을 무시하세요.
                """.formatted(EXPIRE_MINUTES, code);

        mailService.send(req.getEmail(), subject, body);
        return ResponseEntity.ok(safeResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody PasswordResetVerifyRequestDto req,
                                         HttpSession session) {
        String code = (String) session.getAttribute(KEY_CODE);
        Instant exp = (Instant) session.getAttribute(KEY_EXPIRES_AT);

        if (code == null || exp == null) {
            return ResponseEntity.badRequest().body("요청 이력이 없습니다. 먼저 코드 요청을 해주세요.");
        }
        if (Instant.now().isAfter(exp)) {
            clear(session);
            return ResponseEntity.badRequest().body("인증코드가 만료되었습니다. 다시 요청해주세요.");
        }
        if (!req.getCode().equals(code)) {
            return ResponseEntity.badRequest().body("인증코드가 올바르지 않습니다.");
        }
        session.setAttribute(KEY_VERIFIED, Boolean.TRUE);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset(@Valid @RequestBody PasswordResetResetRequestDto req,
                                        HttpSession session) {
        Boolean verified = (Boolean) session.getAttribute(KEY_VERIFIED);
        String email = (String) session.getAttribute(KEY_EMAIL);
        Instant exp = (Instant) session.getAttribute(KEY_EXPIRES_AT);

        if (verified == null || !verified || email == null || exp == null) {
            return ResponseEntity.badRequest().body("인증 절차를 먼저 완료해주세요.");
        }
        if (Instant.now().isAfter(exp)) {
            clear(session);
            return ResponseEntity.badRequest().body("인증이 만료되었습니다. 처음부터 다시 진행해주세요.");
        }

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        clear(session);
        session.invalidate();
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    private void clear(HttpSession session) {
        session.removeAttribute(KEY_EMAIL);
        session.removeAttribute(KEY_CODE);
        session.removeAttribute(KEY_EXPIRES_AT);
        session.removeAttribute(KEY_VERIFIED);
    }

    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
