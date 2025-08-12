package com.example.newsfeed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 팔로우 상태 단건 확인용
public class FollowStatusDto {
    @JsonProperty("조회자 ID") // viewerId → 조회자 ID
    private Long viewerId;

    @JsonProperty("대상자 ID") // targetId → 대상자 ID
    private Long targetId;

    @JsonProperty("내가 팔로우 중")
    private boolean viewerFollowsAccepted;

    @JsonProperty("내 요청 대기중")
    private boolean viewerFollowsPending;

    @JsonProperty("상대 팔로우 중")
    private boolean targetFollowsAccepted;

    @JsonProperty("상대 요청 대기중")
    private boolean targetFollowsPending;

    @JsonProperty("관계 상태")
    private String state;
}

