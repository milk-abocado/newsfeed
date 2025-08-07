package com.example.newsfeed.service;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void softDeleteUser(String username, String password) {
        Users users = usersRepository.findByUsername(username).orElseThrow(() -> new UsersNotFoundException("사용자를 찾을 수 없습니다."));

        if (users.isDeleted()) {
            throw new AlreadyDeletedUsersException("이미 탈퇴된 사용자입니다.");
        }

        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        users.softDelete();
        usersRepository.save(users);
    }
}
