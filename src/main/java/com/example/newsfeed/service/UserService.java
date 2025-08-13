package com.example.newsfeed.service;

import com.example.newsfeed.dto.ChangePasswordRequestDto;
import com.example.newsfeed.dto.FollowListDto;
import com.example.newsfeed.dto.UserProfileUpdateRequestDto;
import com.example.newsfeed.dto.UserProfileResponseDto;
import com.example.newsfeed.entity.ProfileUpdateHistory;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.exception.AlreadyDeletedException;
import com.example.newsfeed.exception.InvalidCredentialsException;
import com.example.newsfeed.exception.PasswordRequiredException;
import com.example.newsfeed.repository.AuthRepository;
import com.example.newsfeed.repository.FollowsRepository;
import com.example.newsfeed.repository.ProfileUpdateHistoryRepository;
import com.example.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.newsfeed.config.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final ProfileUpdateHistoryRepository profileUpdateHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final FollowsRepository followsRepository;

    // 프로필 조회
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId, Long loginUserId) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        // 내가 팔로워한 사람 리스트
        List<Long> myFollowings = followsRepository.findByFollowerIdAndStatus(loginUserId, true)
                .stream()
                .map(f -> f.getFollowing().getId())
                .toList();

        // 타겟 사용자를 팔로워한 사람 리스트
        List<Long> targetFollowers = followsRepository.findByFollowingIdAndStatus(userId, true)
                .stream()
                .map(f -> f.getFollower().getId())
                .toList();

        List<Long> knowIds = myFollowings.stream()
                .filter(targetFollowers::contains)
                .toList();

        List<Users> knowUsers = userRepository.findAllById(knowIds);
        List<FollowListDto> knowFollowers = knowUsers.stream()
                .map(u -> new FollowListDto(u.getId(), u.getNickname()))
                .toList();
        int knowFollower = knowFollowers.size();

        return new UserProfileResponseDto(user, knowFollower, knowFollowers);
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

        if (!Objects.equals(user.getHometown(), dto.getHometown())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("hometown")
                    .oldValue(user.getHometown())
                    .newValue(dto.getHometown())
                    .build());
            user.setHometown(dto.getHometown());
        }

        if (!Objects.equals(user.getSchool(), dto.getSchool())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("school")
                    .oldValue(user.getSchool())
                    .newValue(dto.getSchool())
                    .build());
            user.setSchool(dto.getSchool());
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

    @Transactional
    public void deleteAccount(String email, String password) {

        //1. 비밀번호 미입력(400)
        //1-1) 이메일로 사용자 조회(Users 존재하는지 여부)
        Users users = authRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        //1-2) 비밀번호 미입력
                if (password == null || password.trim().isEmpty()) {
            throw new PasswordRequiredException("비밀번호가 필요합니다.");
        }

        //1-3) 이미 탈퇴한 경우
        if (users.getIsDeleted()) {
            throw new AlreadyDeletedException("이미 탈퇴한 사용자입니다.");
        }

        //2. 비밀번호 불일치(401)
        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        //3. 탈퇴처리(soft delete)
        users.softDelete();
        authRepository.save(users);


        //4. 개인정보 null처리
        String randomPassword = UUID.randomUUID().toString();

        users.setName(null); //이름 삭제
        users.setPassword(passwordEncoder.encode(randomPassword)); //비밀번호 삭제
        users.setBio(null); //소개글 삭제
        users.setNickname(null); //닉네임 삭제
        users.setProfileImage(null); //프로필 사진 삭제
        users.setHometown(null); //지역 삭제
        users.setSchool(null); //학교 삭제
        users.setSecurityQuestion(null);
        users.setSecurityAnswer(null);
        users.setUpdatedAt(LocalDateTime.now());
    }
    }

