package com.example.newsfeed.service.User;


import com.example.newsfeed.entity.User.Email;
import com.example.newsfeed.repository.User.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;

    // 인증 이메일 보내기 (단, 실제로 보낼 수 없으므로 임의로 보낸 token에 접속하면 인증되는 걸로 함)
    public String createToken(String email) {

        Optional<Email> optional = emailRepository.findByEmail(email);

        if (optional.isPresent()) {
            Email existing = optional.get();

            if (existing.isVerified()) {
                throw new IllegalArgumentException("이미 인증된 이메일입니다.");
            }

            // 인증이 안 되었는데, 기존 토큰이 있으면 만료 시간 갱신하거나 기존 토큰 재사용
            existing.setExpirationTime(LocalDateTime.now().plusMinutes(5));
            emailRepository.save(existing);
            return existing.getVerificationToken();
        }

        String token =  UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        Email emailObj = new Email();
        emailObj.setEmail(email);
        emailObj.setVerificationToken(token);
        emailObj.setExpirationTime(expirationTime);
        emailObj.setVerified(false);

        emailRepository.save(emailObj);

        return token;
    }


    // 이메일 인증
    public boolean verifyEmail(String token) {
        Optional<Email> optional = emailRepository.findByVerificationToken(token);

        if (optional.isEmpty()) return false;

        Email verification = optional.get();

        if (verification.getExpirationTime().isBefore(LocalDateTime.now())) {
            return false; // 토큰 만료
        }

        verification.setVerified(true);
        emailRepository.save(verification);
        return true;
    }

    // 인증된 이메일인지 확인
    public boolean isEmailVerified(String email) {
        return emailRepository.findByEmail(email)
                .map(Email::isVerified)
                .orElse(false);
    }


}
