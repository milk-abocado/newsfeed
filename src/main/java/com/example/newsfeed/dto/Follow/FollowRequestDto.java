package com.example.newsfeed.dto.Follow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequestDto {
    private Long followId;     // 팔로우하려는 사용자 ID
}
