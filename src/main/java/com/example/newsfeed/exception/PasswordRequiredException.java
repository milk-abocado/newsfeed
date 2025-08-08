package com.example.newsfeed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) //400반환
public class PasswordRequiredException extends RuntimeException {
    public PasswordRequiredException(String message) {
        super(message);
    }
}
