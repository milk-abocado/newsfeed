package com.example.newsfeed.service;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.exception.AlreadyDeletedException;
import com.example.newsfeed.exception.InvalidCredentialsException;
import com.example.newsfeed.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public void deleteAccount(String name, String password) {
    //비밀번호 미입력(400)
    if (rawPassword == null || rawPassword.trim().isEmpty()) {
        throw new AlreadyDeletedException("비밀번호를 입력해야 합니다.");
    }

    Users user = usersRepository.findByName(name)
            .orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

    if (user.getStatus() == Users.Status.DELETED) {
        throw new AlreadyDeletedException("이미 탈퇴한 계정입니다.");
    }

    //비밀번호 틀림(401)
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        throw new InvalidCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    user.softDelete();
    usersRepository.save(user);
}
