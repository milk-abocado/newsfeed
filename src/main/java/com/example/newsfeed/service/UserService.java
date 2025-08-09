package com.example.newsfeed.service;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.entity.ProfileUpdateHistory;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.ProfileUpdateHistoryRepository;
import com.example.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Users user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserProfileResponseDto(user);
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto dto) {
        Users user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
        Users user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 여부
        if (!Objects.equals(dto.getNewPassword(), dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 길이 체크 (예: 최소 8자)
        if (dto.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        // 새 비밀번호 저장 (암호화)
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}
