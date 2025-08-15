package com.example.newsfeed.service.User;

import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.dto.Follow.FollowListDto;
import com.example.newsfeed.dto.Password.ChangePasswordRequestDto;
import com.example.newsfeed.dto.User.UserProfileResponseDto;
import com.example.newsfeed.dto.User.UserProfileUpdateRequestDto;
import com.example.newsfeed.entity.User.ProfileUpdateHistory;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.exception.AlreadyDeletedException;
import com.example.newsfeed.exception.InvalidCredentialsException;
import com.example.newsfeed.exception.PasswordRequiredException;
import com.example.newsfeed.repository.Auth.AuthRepository;
import com.example.newsfeed.repository.Follow.FollowerBlockRepository;
import com.example.newsfeed.repository.Follow.FollowsRepository;
import com.example.newsfeed.repository.User.ProfileUpdateHistoryRepository;
import com.example.newsfeed.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final FollowerBlockRepository followerBlockRepository;

    /**
     * 프로필 조회
     * @param userId       조회할 사용자 ID
     * @param loginUserId  로그인한 사용자 ID
     */
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId, Long loginUserId) {
        // 내가 상대를 차단한 경우 → 조회 불가
        boolean iBlocked = followerBlockRepository
                .existsByUserIdAndTargetUserId(loginUserId, userId);
        if (iBlocked) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "차단한 사용자입니다.");
        }

        // 상대가 나를 차단한 경우 → 조회 불가
        boolean blockedMe = followerBlockRepository
                .existsByUserIdAndTargetUserId(userId, loginUserId);
        if (blockedMe) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "상대방이 당신을 차단했습니다.");
        }

        // 조회 대상 유저가 존재하고 탈퇴하지 않았는지 확인
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

        // 서로 아는 사람 목록 (교집합)
        List<Long> knowIds = myFollowings.stream()
                .filter(targetFollowers::contains)
                .toList();

        // 교집합 ID로 사용자 정보 조회
        List<Users> knowUsers = userRepository.findAllById(knowIds);
        List<FollowListDto> knowFollowers = knowUsers.stream()
                .map(u -> new FollowListDto(u.getId(), u.getNickname()))
                .toList();
        int knowFollower = knowFollowers.size();

        // 프로필 DTO 생성 후 반환
        return new UserProfileResponseDto(user, knowFollower, knowFollowers);
    }

    /**
     * 프로필 수정
     * 변경된 필드가 있을 경우 변경 이력을 저장하고 값 업데이트
     */
    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequestDto dto) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        // 닉네임 변경
        if (!Objects.equals(user.getNickname(), dto.getNickname())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("nickname")
                    .oldValue(user.getNickname())
                    .newValue(dto.getNickname())
                    .build());
            user.setNickname(dto.getNickname());
        }

        // 자기소개 변경
        if (!Objects.equals(user.getBio(), dto.getBio())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("bio")
                    .oldValue(user.getBio())
                    .newValue(dto.getBio())
                    .build());
            user.setBio(dto.getBio());
        }

        // 프로필 이미지 변경
        if (!Objects.equals(user.getProfileImage(), dto.getProfileImage())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("profileImage")
                    .oldValue(user.getProfileImage())
                    .newValue(dto.getProfileImage())
                    .build());
            user.setProfileImage(dto.getProfileImage());
        }

        // 고향 변경
        if (!Objects.equals(user.getHometown(), dto.getHometown())) {
            profileUpdateHistoryRepository.save(ProfileUpdateHistory.builder()
                    .user(user)
                    .fieldName("hometown")
                    .oldValue(user.getHometown())
                    .newValue(dto.getHometown())
                    .build());
            user.setHometown(dto.getHometown());
        }

        // 학교 변경
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

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto dto) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        String current = dto.getCurrentPassword();
        String next = dto.getNewPassword();
        String confirm = dto.getConfirmNewPassword();

        // 기본 유효성 검사
        if (current == null || next == null || confirm == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호 입력이 올바르지 않습니다."
            );
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(current, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "현재 비밀번호가 일치하지 않습니다."
            );
        }

        // 새 비밀번호 형식 검증
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

        // 변경 저장
        user.setPassword(passwordEncoder.encode(next));
    }

    /**
     * 비밀번호 형식 검사 (영문/숫자/특수문자 포함, 8~64자)
     */
    private boolean isValidPassword(String pw) {
        if (pw == null || pw.length() < 8 || pw.length() > 64) return false;
        boolean hasLetter = pw.chars().anyMatch(Character::isLetter);
        boolean hasDigit  = pw.chars().anyMatch(Character::isDigit);
        boolean hasSpec   = pw.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        return hasLetter && hasDigit && hasSpec;
    }

    /**
     * 회원 탈퇴(Soft Delete + 개인정보 초기화)
     */
    @Transactional
    public void deleteAccount(String email, String password) {

        // 1. 이메일로 사용자 조회 (없으면 예외
        Users users = authRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 미입력 시 예외
        if (password == null || password.trim().isEmpty()) {
            throw new PasswordRequiredException("비밀번호가 필요합니다.");
        }

        // 3. 이미 탈퇴한 계정인지 확인
        if (users.getIsDeleted()) {
            throw new AlreadyDeletedException("이미 탈퇴한 사용자입니다.");
        }

        // 4. 비밀번호 불일치 시 예외
        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 5. 탈퇴 처리 (soft delete)
        users.softDelete();
        authRepository.save(users);


        // 6. 개인정보 초기화
        String randomPassword = UUID.randomUUID().toString();
        users.setName(null);                // 이름 삭제
        users.setPassword(passwordEncoder.encode(randomPassword)); // 비밀번호 삭제
        users.setBio(null);                 // 소개글 삭제
        users.setNickname(null);            // 닉네임 삭제
        users.setProfileImage(null);        // 프로필 사진 삭제
        users.setHometown(null);            // 지역 삭제
        users.setSchool(null);              // 학교 삭제
        users.setSecurityQuestion(null);    // 보안 질문 삭제
        users.setSecurityAnswer(null);      // 보안 답변 삭제
        users.setUpdatedAt(LocalDateTime.now());    // 수정일 갱신
    }
}
