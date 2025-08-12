package com.example.newsfeed.repository;

// Users 엔티티의 모든 필드를 조회하지 않고,
// 필요한 일부 필드(id, nickname, profileImage)만 선택적으로 조회하기 위해 사용
public interface UserSummary {
    Long getId();
    String getNickname();
    String getProfileImage();
}