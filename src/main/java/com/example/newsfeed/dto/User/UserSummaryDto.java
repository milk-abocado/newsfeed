package com.example.newsfeed.dto.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 목록 아이템(사용자 카드) + 관계 플래그
public class UserSummaryDto {
    @JsonProperty("사용자 ID")
    private Long id;

    @JsonProperty("닉네임")
    private String nickname;

    @JsonProperty("프로필 이미지")
    private String profileImage; // 없으면 null 허용

    // 관계 플래그 (viewer = 현재 로그인 사용자)
    @JsonProperty("내가 팔로우 중")
    private boolean viewerFollowsUserAccepted; // 내가 그 사람을 '수락 완료' 팔로우/친구 상태
    @JsonProperty("내 요청 대기 중")
    private boolean viewerFollowsUserPending;  // 내가 보낸 요청이 '대기중'

    @JsonProperty("상대 팔로우 중")
    private boolean userFollowsViewerAccepted; // 그 사람이 나를 '수락 완료'로 팔로우/친구
    @JsonProperty("상대 요청 대기 중")
    private boolean userFollowsViewerPending;  // 그 사람이 보낸 요청이 '대기중'

    @JsonProperty("맞팔 여부")
    public boolean isMutual() {
        return viewerFollowsUserAccepted && userFollowsViewerAccepted;
    }
}
