package com.example.newsfeed.controller;

import com.example.newsfeed.dto.FollowStatusDto;
import com.example.newsfeed.dto.UserSummaryDto;
import com.example.newsfeed.dto.UserSummaryPageResponseDto;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.UserRepository;
import com.example.newsfeed.service.FollowReadService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowReadController {

    private final FollowReadService followReadService;
    private final UserRepository userRepository;

    /**
     * > 팔로잉 목록 조회
     * - 특정 userId가 '팔로우하는 사용자' 목록을 반환
     * - accepted 파라미터로 '수락된 친구(true)' 또는 '대기중 요청(false)'만 필터링 가능
     * - 로그인한 사용자 본인만 자신의 목록을 조회할 수 있음
     *
     * 예시 요청: GET /follows/{userId}/following?accepted=true&page=0&size=5
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "true") boolean accepted,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpSession session) {

        // 세션에서 로그인 사용자 조회
        Users viewer = (Users) session.getAttribute("user");
        if (viewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        // 본인만 자신의 팔로잉 목록 조회 가능
        if (!viewer.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 조회할 수 있습니다.");
        }

        // 최신 id 기준 내림차순 정렬 + 페이지네이션
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserSummaryDto> pageResult =
                followReadService.getFollowingList(viewer.getId(), userId, accepted, pageable);

        return ResponseEntity.ok(toResponse(pageResult));
    }

    /**
     * > 팔로워 목록 조회
     * - 특정 userId를 '팔로우하는 사용자' 목록을 반환
     * - accepted 파라미터로 '수락된 친구(true)' 또는 '대기중 요청(false)'만 필터링 가능
     * - 로그인한 사용자 본인만 자신의 목록을 조회할 수 있음
     *
     * 예시 요청: GET /follows/{userId}/followers?accepted=false&page=0&size=5
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "true") boolean accepted,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpSession session) {

        Users viewer = (Users) session.getAttribute("user");
        if (viewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        // 본인만 자신의 팔로워 목록 조회 가능
        if (!viewer.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 조회할 수 있습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserSummaryDto> pageResult =
                followReadService.getFollowerList(viewer.getId(), userId, accepted, pageable);

        return ResponseEntity.ok(toResponse(pageResult));
    }

    /**
     * > 팔로우 상태 단건 조회
     * - 로그인한 사용자(viewer)와 targetId 간의 팔로우/친구 상태를 조회
     * - 본인 자신을 대상으로 조회할 수 없음
     *
     * 예시 요청: GET /follows/status/targetId}
     */
    @GetMapping("/status/{targetId}")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long targetId, HttpSession session) {
        Users viewer = (Users) session.getAttribute("user");
        if (viewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        if (viewer.getId().equals(targetId)) {
            return ResponseEntity.badRequest().body("본인 대상 상태 조회는 의미가 없습니다.");
        }
        if (!userRepository.existsById(targetId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        FollowStatusDto status = followReadService.getFollowStatus(viewer.getId(), targetId);
        return ResponseEntity.ok(status);
    }

    // ===== 공통 변환: Page -> 한글 응답 DTO =====
    private UserSummaryPageResponseDto toResponse(Page<UserSummaryDto> page) {
        return new UserSummaryPageResponseDto(
                page.getContent(),          // 목록
                page.getNumber(),           // 현재 페이지
                page.getSize(),             // 페이지 크기
                page.getTotalPages(),       // 총 페이지 수
                page.getTotalElements(),    // 총 요소 수
                page.isFirst(),             // 첫 페이지 여부
                page.isLast()               // 마지막 페이지 여부
        );
    }
}
