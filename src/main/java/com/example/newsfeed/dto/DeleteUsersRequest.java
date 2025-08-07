package com.example.newsfeed.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

@Getter
@Setter
public class DeleteUsersRequest {
    private String password;
    private String username;
}
