package com.example.newsfeed.service;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;

    public void softDeleteUser(String username) {
        Users users = usersRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(() -> new UsersNotFoundException("존재하지 않거나 이미 삭제된 사용자입니다."));

        users.softDelete();
        usersRepository.save(users);
    }
}
