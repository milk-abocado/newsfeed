package com.example.newsfeed.controller.Password;

import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.Password.PasswordResetForgotRequestDto;
import com.example.newsfeed.dto.Password.PasswordResetResetRequestDto;
import com.example.newsfeed.dto.Password.PasswordResetVerifyRequestDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.repository.Auth.AuthRepository;
import com.example.newsfeed.repository.User.UserRepository;
import com.example.newsfeed.service.User.MailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth/password/session")
@RequiredArgsConstructor
public class SessionPasswordResetController {

    // ===== 세션에 저장할 키 상수 =====
    private static final String KEY_EMAIL = "PR_EMAIL";              // 요청한 이메일
    private static final String KEY_CODE = "PR_CODE";                // 발급된 인증 코드
    private static final String KEY_EXPIRES_AT = "PR_EXPIRES_AT";    // 인증 코드 만료 시각
    private static final String KEY_VERIFIED = "PR_VERIFIED";        // 인증 완료 여부
    private static final int EXPIRE_MINUTES = 10;                    // 인증 코드 유효 시간(분)

    private final MailService mailService;                  // 메일 전송 서비스
    private final UserRepository userRepository;            // 사용자 조회/저장
    private final AuthRepository authRepository;            // 로그인 인증 관련 조회
    private final PasswordEncoder passwordEncoder;          // 비밀번호 암호화
    private final SecureRandom random = new SecureRandom(); // 인증 코드 생성용 랜덤 객체

    // 비밀번호 형식: 영문 + 숫자 + 특수문자 포함, 최소 8자
    private static final Pattern password_Pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    /**
     * STEP 1 - 비밀번호 재설정 요청 (이메일/질문/답변 확인 후 인증 코드 발급 & 메일 발송)
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgot(@Valid @RequestBody PasswordResetForgotRequestDto req,
                                         HttpSession session) {
        try {
            // 1. 이메일로 사용자 검색 (삭제된 계정 제외 안 함)
            Users user = authRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

            // 2. 비밀번호 찾기용 질문 검증
            if (!user.getSecurityQuestion().equals(req.getSecurityQuestion())) {
                throw new IllegalArgumentException("질문이 일치하지 않습니다.");
            }

            // 3. 비밀번호 찾기용 답변 검증
            if (!user.getSecurityAnswer().equals(req.getSecurityAnswer())) {
                throw new IllegalArgumentException("답이 일치하지 않습니다.");
            }

            // 안전한 응답 메시지(보안상 이메일 존재 여부 노출 방지)
            String safeResponse = "비밀번호 재설정 안내가 전송되었다면 곧 도착합니다.";
            if (!StringUtils.hasText(req.getEmail())) return ResponseEntity.ok(safeResponse);

            // 삭제되지 않은 사용자만 조회
            Optional<Users> userOpt = userRepository.findByEmailAndIsDeletedFalse(req.getEmail());
            if (userOpt.isEmpty()) return ResponseEntity.ok(safeResponse);

            // 4. 인증코드 & 만료시각 생성
            String code = generate6DigitCode();
            Instant expiresAt = Instant.now().plus(EXPIRE_MINUTES, ChronoUnit.MINUTES);

            // 5. 세션에 요청 데이터 저장
            session.setAttribute(KEY_EMAIL, req.getEmail());
            session.setAttribute(KEY_CODE, code);
            session.setAttribute(KEY_EXPIRES_AT, expiresAt);
            session.setAttribute(KEY_VERIFIED, Boolean.FALSE);

            // 6. 메일 전송
            String subject = "[Newsfeed] 비밀번호 재설정 인증 코드";
            String body = """
                    아래 인증 코드를 입력해 비밀번호 재설정을 진행해 주세요. (유효 시간 %d분)
                    인증코드: %s
                    
                    본인이 요청하지 않았다면 이 메일을 무시하세요.
                    """.formatted(EXPIRE_MINUTES, code);

            mailService.send(req.getEmail(), subject, body);
            return ResponseEntity.ok(safeResponse);
        }
        catch (IllegalArgumentException e) {
            // 입력 값 불일치 시 400 응답
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * STEP 2 - 인증코드 검증
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody PasswordResetVerifyRequestDto req,
                                         HttpSession session) {
        // 세션에서 발급된 코드/만료시각 조회
        String code = (String) session.getAttribute(KEY_CODE);
        Instant exp = (Instant) session.getAttribute(KEY_EXPIRES_AT);

        if (code == null || exp == null) {
            return ResponseEntity.badRequest().body("요청 이력이 없습니다.");
        }
        if (Instant.now().isAfter(exp)) {
            clear(session);
            return ResponseEntity.badRequest().body("인증 코드가 만료되었습니다.");
        }
        if (!req.getCode().equals(code)) {
            return ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
        }
        session.setAttribute(KEY_VERIFIED, Boolean.TRUE);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    /**
     * STEP 3 - 비밀번호 재설정
     */
    @PostMapping("/reset")
    public ResponseEntity<String> reset(@Valid @RequestBody PasswordResetResetRequestDto req,
                                        HttpSession session) {
        // 세션에서 인증 상태/이메일/만료 시각 조회
        Boolean verified = (Boolean) session.getAttribute(KEY_VERIFIED);
        String email = (String) session.getAttribute(KEY_EMAIL);
        Instant exp = (Instant) session.getAttribute(KEY_EXPIRES_AT);

        if (verified == null || !verified || email == null || exp == null) {
            return ResponseEntity.badRequest().body("인증 절차를 먼저 완료해 주세요.");
        }
        if (Instant.now().isAfter(exp)) {
            clear(session);
            return ResponseEntity.badRequest().body("인증이 만료되었습니다. 처음부터 다시 진행해 주세요.");
        }

        // 새 비밀번호 형식 검사
        String newPassword = req.getNewPassword();
        if (!password_Pattern.matcher(newPassword).matches()) {
            return ResponseEntity.badRequest().body(
                    "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자(!@#$%^&*)를 모두 포함해야 합니다."
            );
        }

        Users user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경 & 저장
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        // 세션 초기화 & 무효화
        clear(session);
        session.invalidate();
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    /**
     * 세션 데이터 초기화
     */
    private void clear(HttpSession session) {
        session.removeAttribute(KEY_EMAIL);
        session.removeAttribute(KEY_CODE);
        session.removeAttribute(KEY_EXPIRES_AT);
        session.removeAttribute(KEY_VERIFIED);
    }

    /**
     * 6자리 인증코드 생성
     */
    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
