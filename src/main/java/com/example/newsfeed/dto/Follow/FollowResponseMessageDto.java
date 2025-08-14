package com.example.newsfeed.dto.Follow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseMessageDto {
    // 응답 메시지 (예: "친구 요청이 완료되었습니다.")
    private String message;

    // 매개변수가 있는 생성자
    // @param message: 응답 메시지 내용
    public FollowResponseMessageDto(String message) {
        this.message = message;
    }
}
