package com.example.newsfeed.controller;

import com.example.newsfeed.entity.Users;
import com.example.newsfeed.dto.UsersRequestDto;
import com.example.newsfeed.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UsersRequestDto request) {
        try {
            Users user = usersService.signup(request);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
