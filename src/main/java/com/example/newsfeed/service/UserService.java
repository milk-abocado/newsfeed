package com.example.newsfeed.service;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.entity.ProfileUpdateHistory;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.ProfileUpdateHistoryRepository;
import com.example.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.newsfeed.config.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileUpdateHistoryRepository profileUpdateHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    // 프로필 조회
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));
        return new UserProfileResponseDto(user);
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto dto) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        if (!Objects.equals(user.getNickname(), dto.getNickname())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("nickname")
                    .oldValue(user.getNickname())
                    .newValue(dto.getNickname())
                    .build());
            user.setNickname(dto.getNickname());
        }

        if (!Objects.equals(user.getBio(), dto.getBio())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("bio")
                    .oldValue(user.getBio())
                    .newValue(dto.getBio())
                    .build());
            user.setBio(dto.getBio());
        }

        if (!Objects.equals(user.getProfileImage(), dto.getProfileImage())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("profileImage")
                    .oldValue(user.getProfileImage())
                    .newValue(dto.getProfileImage())
                    .build());
            user.setProfileImage(dto.getProfileImage());
        }
    }

    // 비밀번호 변경 기능
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto dto) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        String current = dto.getCurrentPassword();
        String next = dto.getNewPassword();
        String confirm = dto.getConfirmNewPassword();

        // 기본 유효성
        if (current == null || next == null || confirm == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호 입력이 올바르지 않습니다."
            );
        }

        // 현재 비밀번호 불일치
        if (!passwordEncoder.matches(current, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "현재 비밀번호가 일치하지 않습니다."
            );
        }

        // 비밀번호 형식 검증 (예: 8~64자, 영문/숫자/특수문자 포함)
        if (!isValidPassword(next)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호 형식이 올바르지 않습니다. (8~64자, 영문·숫자·특수문자 포함)"
            );
        }

        // 현재 비밀번호와 동일 금지
        if (passwordEncoder.matches(next, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."
            );
        }

        // 새 비밀번호-확인 비밀번호 일치
        if (!Objects.equals(next, confirm)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "새 비밀번호와 확인 비밀번호가 일치하지 않습니다."
            );
        }

        // 저장
        user.setPassword(passwordEncoder.encode(next));
    }

    // 영문/숫자/특수문자 각 1개 이상
    private boolean isValidPassword(String pw) {
        if (pw == null || pw.length() < 8 || pw.length() > 64) return false;
        boolean hasLetter = pw.chars().anyMatch(Character::isLetter);
        boolean hasDigit  = pw.chars().anyMatch(Character::isDigit);
        boolean hasSpec   = pw.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        return hasLetter && hasDigit && hasSpec;
    }
}

