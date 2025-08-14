package com.example.newsfeed.controller.User;

import com.example.newsfeed.dto.Auth.AuthLoginRequestDto;
import com.example.newsfeed.dto.Password.ChangePasswordRequestDto;
import com.example.newsfeed.dto.User.UserProfileResponseDto;
import com.example.newsfeed.dto.User.UserProfileUpdateRequestDto;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.exception.AlreadyDeletedException;
import com.example.newsfeed.exception.InvalidCredentialsException;
import com.example.newsfeed.exception.PasswordRequiredException;
import com.example.newsfeed.service.User.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private ResponseEntity<String> msg(HttpStatus status, String body) {
        return ResponseEntity.status(status).body(body);
    }

    // ===== 프로필 조회 =====
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId,
                                                 HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");

        if (loginUser == null) {
            return msg(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            UserProfileResponseDto profile = userService.getUserProfile(userId, loginUser.getId());
            return ResponseEntity.ok(profile);
        } catch (ResponseStatusException e) {
            return msg((HttpStatus) e.getStatusCode(), e.getReason());
        } catch (InvalidCredentialsException | PasswordRequiredException | AlreadyDeletedException e) {
            // 혹시 서비스에서 커스텀 예외가 던져져도 문자열로 반환
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return msg(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 프로필 수정 =====
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUserProfile(@PathVariable Long userId,
                                    @RequestBody UserProfileUpdateRequestDto dto,
                                    HttpSession session) {
        Users loginUser = (Users) session.getAttribute("user");

        if (loginUser == null) {
            return msg(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!loginUser.getId().equals(userId)) {
            return msg(HttpStatus.FORBIDDEN, "자기 자신만 프로필을 수정할 수 있습니다.");
        }

        try {
            userService.updateUserProfile(loginUser.getId(), dto);
            return msg(HttpStatus.OK, "프로필 수정이 완료되었습니다.");
        } catch (ResponseStatusException e) {
            return msg((HttpStatus) e.getStatusCode(), e.getReason());
        } catch (InvalidCredentialsException | PasswordRequiredException | AlreadyDeletedException |
                 IllegalArgumentException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return msg(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 비밀번호 변경 =====
    @PatchMapping("/{userId}/change")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId,
                                 HttpSession session,
                                 @RequestBody ChangePasswordRequestDto dto) {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            return msg(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!user.getId().equals(userId)) {
            return msg(HttpStatus.FORBIDDEN, "자기 자신만 비밀번호를 변경할 수 있습니다.");
        }

        try {
            userService.changePassword(user.getId(), dto);
            return msg(HttpStatus.OK, "비밀번호가 변경되었습니다.");
        } catch (ResponseStatusException e) {
            return msg((HttpStatus) e.getStatusCode(), e.getReason());
        } catch (InvalidCredentialsException | PasswordRequiredException | AlreadyDeletedException e) {
            // 현재 changePassword는 주로 ResponseStatusException을 던지지만 방어적으로 처리
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return msg(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // ===== 회원 탈퇴 =====
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody AuthLoginRequestDto request,
                                             HttpSession session) {

        Users loginUser = (Users) session.getAttribute("user");

        if (loginUser == null) {
            return msg(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        try {
            userService.deleteAccount(request.getEmail(), request.getPassword());
            return msg(HttpStatus.OK, "회원 탈퇴가 완료되었습니다.");
        } catch (InvalidCredentialsException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());      // 400: 잘못된 아이디/비번
        } catch (PasswordRequiredException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());      // 400: 비밀번호 미입력
        } catch (AlreadyDeletedException e) {
            return msg(HttpStatus.CONFLICT, e.getMessage());         // 409: 이미 탈퇴
        } catch (ResponseStatusException e) {
            return msg((HttpStatus) e.getStatusCode(), e.getReason());
        } catch (IllegalArgumentException e) {
            return msg(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return msg(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }
}
