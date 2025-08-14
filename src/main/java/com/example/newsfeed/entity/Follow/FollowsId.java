package com.example.newsfeed.entity.Follow;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowsId implements Serializable {

    private Long followerId;  // followerId 필드 추가
    private Long followingId; // followingId 필드 추가

    // 기본 생성자
    public FollowsId() {}

    // 생성자
    public FollowsId(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    // equals()와 hashCode() 메서드 오버라이드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowsId followsId = (FollowsId) o;
        return Objects.equals(followerId, followsId.followerId) &&
                Objects.equals(followingId, followsId.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followingId);
    }
}

