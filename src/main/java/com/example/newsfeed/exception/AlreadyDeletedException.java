package com.example.newsfeed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) //409반환
public class AlreadyDeletedException extends RuntimeException {
    public AlreadyDeletedException(String message) {
        super(message);
    }
}
