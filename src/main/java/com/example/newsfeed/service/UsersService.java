package com.example.newsfeed.service;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.exception.AlreadyDeletedException;
import com.example.newsfeed.exception.InvalidCredentialsException;
import com.example.newsfeed.exception.PasswordRequiredException;
import com.example.newsfeed.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void deleteAccount(String email, String password) {

        //1. 비밀번호 미입력(400)
        //1-1) 이메일로 사용자 조회(Users 존재하는지 여부)
        Users users = usersRepository.findByName(email)
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
        usersRepository.save(users);
    }
}