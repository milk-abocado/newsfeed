package com.example.newsfeed.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUsersRequest {
    private String password;
    private String email;
}
