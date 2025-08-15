package com.example.newsfeed.controller.Auth;

import com.example.newsfeed.dto.Auth.AuthLoginRequestDto;
import com.example.newsfeed.dto.Auth.AuthRequestDto;
import com.example.newsfeed.entity.User.Email;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.repository.User.EmailRepository;
import com.example.newsfeed.service.Auth.AuthService;
import com.example.newsfeed.service.User.EmailService;
import com.example.newsfeed.service.User.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final EmailRepository emailRepository;
    private final MailService mailService;
    private final SecureRandom random = new SecureRandom();
    private static final int EXPIRE_MINUTES = 5;

    private ResponseEntity<String> str(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(msg);
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequestDto request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            return str(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다.");
        }
        try {
            Users user = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (ResponseStatusException ex) {
            HttpStatus s = (HttpStatus) ex.getStatusCode();
            if (s == HttpStatus.BAD_REQUEST) {
                return str(HttpStatus.BAD_REQUEST, ex.getReason() != null ? ex.getReason() : "요청이 올바르지 않습니다.");
            } else if (s == HttpStatus.CONFLICT) {
                return str(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
            }
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 로그인 =====
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto request, HttpSession session) {
        try {
            Users user = authService.login(request.getEmail(), request.getPassword());
            session.setAttribute("user", user);
            return ResponseEntity.ok("로그인에 성공했습니다.");
        } catch (ResponseStatusException ex) {
            HttpStatus s = (HttpStatus) ex.getStatusCode();
            if (s == HttpStatus.BAD_REQUEST) {
                return str(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 모두 입력해 주세요.");
            } else if (s == HttpStatus.NOT_FOUND) {
                return str(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다.");
            } else if (s == HttpStatus.FORBIDDEN) {
                return str(HttpStatus.FORBIDDEN, "탈퇴한 사용자입니다.");
            } else if (s == HttpStatus.UNAUTHORIZED) {
                return str(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
            }
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 로그아웃 =====
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            return str(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Users user = (Users) userObj;  // 캐스팅
        Long userId = user.getId();
        String email = user.getEmail();

        session.invalidate();
        return ResponseEntity.ok("User " + userId + " (" + email + "): 로그아웃이 완료되었습니다.");
    }

    // ===== 이메일 인증 코드 발송 =====
    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody Map<String, String> req) {
        String emailAddr = req.get("email");
        if (emailAddr == null || emailAddr.isBlank()) {
            return ResponseEntity.badRequest().body("이메일을 입력해 주세요.");
        }

        try {
            Optional<Email> optionalEmail = emailRepository.findByEmail(emailAddr);
            if (optionalEmail.isPresent() && optionalEmail.get().isVerified()) {
                return str(HttpStatus.BAD_REQUEST, "이미 인증된 이메일입니다.");
            }

            String token = generate6DigitCode();
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

            Email emailEntity = emailRepository.findByEmail(emailAddr).orElse(new Email());
            emailEntity.setEmail(emailAddr);
            emailEntity.setVerificationToken(token);
            emailEntity.setExpirationTime(expiration);
            emailEntity.setVerified(false);
            emailRepository.save(emailEntity);

            String subject = "[Newsfeed] 회원 가입 이메일 인증 코드";
            String body = """
                    아래 인증 코드를 입력하여 회원 가입을 완료해 주세요. (유효 시간 %d분)
                    인증 코드: %s
                    """.formatted(EXPIRE_MINUTES, token);

            mailService.send(emailAddr, subject, body);
            return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 이메일 인증 =====
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> req) {
        String emailAddr = req.get("email");
        String code = req.get("code");

        if (emailAddr == null || emailAddr.isBlank() || code == null || code.isBlank()) {
            return str(HttpStatus.BAD_REQUEST, "이메일과 인증 코드를 모두 입력해 주세요.");
        }

        try {
            Email emailEntity = emailRepository.findByEmail(emailAddr).orElse(null);
            if (emailEntity == null) {
                return str(HttpStatus.BAD_REQUEST, "인증 요청 이력이 없습니다.");
            }
            if (LocalDateTime.now().isAfter(emailEntity.getExpirationTime())) {
                return str(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다.");
            }
            if (!emailEntity.getVerificationToken().equals(code)) {
                return str(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다.");
            }

            emailEntity.setVerified(true);
            emailRepository.save(emailEntity);
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } catch (Exception e) {
            return str(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
