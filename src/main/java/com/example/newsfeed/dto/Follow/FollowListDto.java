package com.example.newsfeed.dto.Follow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowListDto {
    private Long followerId;        // 팔로워 ID
    private String followerName;    // 팔로워 이름

    // 생성자
    public FollowListDto(Long followerId, String followerName) {
        this.followerId = followerId;
        this.followerName = followerName;
    }
}
