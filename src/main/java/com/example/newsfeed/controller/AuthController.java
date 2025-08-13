package com.example.newsfeed.controller;

import com.example.newsfeed.dto.AuthLoginRequestDto;
import com.example.newsfeed.entity.Email;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.dto.AuthRequestDto;
import com.example.newsfeed.repository.EmailRepository;
import com.example.newsfeed.service.EmailService;
import com.example.newsfeed.service.AuthService;
import com.example.newsfeed.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody Map<String, String> req) {
        String emailAddr = req.get("email");
        if (emailAddr == null || emailAddr.isBlank()) {
            return ResponseEntity.badRequest().body("이메일을 입력해주세요.");
        }

        Optional<Email> optionalEmail = emailRepository.findByEmail(emailAddr);
        if (optionalEmail.isPresent() && optionalEmail.get().isVerified()) {
            return ResponseEntity.badRequest().body("이미 인증된 이메일입니다.");
        }

        String token = generate6DigitCode();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        Email emailEntity = emailRepository.findByEmail(emailAddr)
                .orElse(new Email());
        emailEntity.setEmail(emailAddr);
        emailEntity.setVerificationToken(token);
        emailEntity.setExpirationTime(expiration);
        emailEntity.setVerified(false);
        emailRepository.save(emailEntity);

        String subject = "[Newsfeed] 회원가입 이메일 인증코드";
        String body = """
                아래 인증코드를 입력하여 회원가입을 완료해 주세요. (유효시간 %d분)
                인증코드: %s
                """.formatted(EXPIRE_MINUTES, token);

        mailService.send(emailAddr, subject, body);
        return ResponseEntity.ok("인증코드가 이메일로 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> req) {
        String emailAddr = req.get("email");
        String code = req.get("code");

        Email emailEntity = emailRepository.findByEmail(emailAddr)
                .orElse(null);
        if (emailEntity == null) {
            return ResponseEntity.badRequest().body("인증 요청 이력이 없습니다.");
        }
        if (LocalDateTime.now().isAfter(emailEntity.getExpirationTime())) {
            return ResponseEntity.badRequest().body("인증코드가 만료되었습니다.");
        }
        if (!emailEntity.getVerificationToken().equals(code)) {
            return ResponseEntity.badRequest().body("인증코드가 올바르지 않습니다.");
        }

        emailEntity.setVerified(true);
        emailRepository.save(emailEntity);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
