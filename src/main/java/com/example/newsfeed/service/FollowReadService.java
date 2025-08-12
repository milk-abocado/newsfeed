package com.example.newsfeed.service;

import com.example.newsfeed.dto.FollowStatusDto;
import com.example.newsfeed.dto.UserSummaryDto;
import com.example.newsfeed.repository.FollowsRepository;
import com.example.newsfeed.repository.UserRepository;
import com.example.newsfeed.repository.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowReadService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    // ===== 팔로잉 목록 (userId가 팔로우하는 사람들) =====
    public Page<UserSummaryDto> getFollowingList(Long viewerId, Long userId, Boolean accepted, Pageable pageable) {
        Page<UserSummary> page = followsRepository.findFollowingUsers(userId, accepted, pageable);
        return attachRelationFlags(viewerId, page);
    }

    // ===== 팔로워 목록 (userId를 팔로우하는 사람들) =====
    public Page<UserSummaryDto> getFollowerList(Long viewerId, Long userId, Boolean accepted, Pageable pageable) {
        Page<UserSummary> page = followsRepository.findFollowerUsers(userId, accepted, pageable);
        return attachRelationFlags(viewerId, page);
    }

    // ===== 단건 상태 확인 =====
    // -> viewerId와 targetId 간의 관계를 한글 상태로 반환
    // -> 예: 서로 친구, 보낸 요청 대기중, 받은 요청 대기중, 팔로잉 중, 나를 팔로우 중, 관계 없음
    public FollowStatusDto getFollowStatus(Long viewerId, Long targetId) {
        // viewer → target
        boolean viewerAccepted = followsRepository
                .existsByFollowerIdAndFollowingIdAndStatus(viewerId, targetId, true);
        boolean viewerPending = followsRepository
                .existsByFollowerIdAndFollowingIdAndStatus(viewerId, targetId, false);

        // target → viewer
        boolean targetAccepted = followsRepository
                .existsByFollowerIdAndFollowingIdAndStatus(targetId, viewerId, true);
        boolean targetPending = followsRepository
                .existsByFollowerIdAndFollowingIdAndStatus(targetId, viewerId, false);

        // 관계 상태를 한글로 변환
        String stateKorean;
        if (viewerAccepted && targetAccepted) stateKorean = "서로 친구";
        else if (viewerPending)               stateKorean = "보낸 요청 대기중";
        else if (targetPending)               stateKorean = "받은 요청 대기중";
        else if (viewerAccepted)              stateKorean = "팔로잉 중";
        else if (targetAccepted)              stateKorean = "나를 팔로우 중";
        else                                  stateKorean = "관계 없음";

        // DTO로 변환 후 반환
        return FollowStatusDto.builder()
                .viewerId(viewerId)
                .targetId(targetId)
                .viewerFollowsAccepted(viewerAccepted)
                .viewerFollowsPending(viewerPending)
                .targetFollowsAccepted(targetAccepted)
                .targetFollowsPending(targetPending)
                .state(stateKorean) // 한글로 직접 설정
                .build();
    }

    // ===== 내부 유틸: 목록에 관계 플래그 붙이기 =====
    // 목록에서 각 사용자가 viewer와 어떤 관계인지 플래그(accepted, pending)를 추가
    // // 배치 조회를 사용해 N+1 문제 방지
    private Page<UserSummaryDto> attachRelationFlags(Long viewerId, Page<UserSummary> page) {
        // 조회된 사용자들의 ID 목록
        List<Long> ids = page.getContent().stream().map(UserSummary::getId).toList();

        // viewer → target 관계 조회
        Map<Long, Boolean> viewerAcceptedMap = new HashMap<>();
        Map<Long, Boolean> viewerPendingMap  = new HashMap<>();
        followsRepository.findAllByFollowerIdAndFollowingIdIn(viewerId, ids)
                .forEach(f -> {
                    if (Boolean.TRUE.equals(f.getStatus())) viewerAcceptedMap.put(f.getFollowingId(), true);
                    else viewerPendingMap.put(f.getFollowingId(), true);
                });

        // target → viewer 관계 조회
        Map<Long, Boolean> targetAcceptedMap = new HashMap<>();
        Map<Long, Boolean> targetPendingMap  = new HashMap<>();
        followsRepository.findAllByFollowerIdInAndFollowingId(ids, viewerId)
                .forEach(f -> {
                    if (Boolean.TRUE.equals(f.getStatus())) targetAcceptedMap.put(f.getFollowerId(), true);
                    else targetPendingMap.put(f.getFollowerId(), true);
                });

        // UserSummary → UserSummaryDto 변환 시 관계 플래그 추가
        List<UserSummaryDto> items = page.getContent().stream().map(u ->
                UserSummaryDto.builder()
                        .id(u.getId())
                        .nickname(u.getNickname())
                        .profileImage(u.getProfileImage())
                        .viewerFollowsUserAccepted(viewerAcceptedMap.getOrDefault(u.getId(), false))
                        .viewerFollowsUserPending(viewerPendingMap.getOrDefault(u.getId(), false))
                        .userFollowsViewerAccepted(targetAcceptedMap.getOrDefault(u.getId(), false))
                        .userFollowsViewerPending(targetPendingMap.getOrDefault(u.getId(), false))
                        .build()
        ).collect(Collectors.toList());

        // 원래의 페이지 정보(pageable, totalElements) 유지하면서 DTO로 변환된 페이지 반환
        return new PageImpl<>(items, page.getPageable(), page.getTotalElements());
    }
}