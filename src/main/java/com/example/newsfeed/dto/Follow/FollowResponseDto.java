package com.example.newsfeed.dto.Follow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDto {
    // 친구 요청을 보낸 사람의 ID
    private Long followerId;

    // 친구 요청을 받은 사람의 ID
    private Long followingId;

    // 친구 요청 상태 (수락: true, 대기: false)
    private Boolean status;

    // 매개변수가 있는 생성자
    // @param followerId: 친구 요청을 보낸 사람의 ID
    // @param followingId: 친구 요청을 받은 사람의 ID
    // @param status: 친구 요청의 상태 (TRUE: 수락, FALSE: 대기)
    public FollowResponseDto(Long followerId, Long followingId, Boolean status) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.status = status;
    }
}
