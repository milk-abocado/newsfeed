package com.example.newsfeed.controller;

import com.example.newsfeed.dto.FollowListDto;
import com.example.newsfeed.dto.FollowRequestDto;
import com.example.newsfeed.dto.FollowResponseMessageDto;
import com.example.newsfeed.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Internal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/followers")
public class FollowController {

    private final FollowService followService;

    // 친구 요청 (POST /followers/{userId}/following)
    // -> 사용자가 다른 사용자에게 친구 요청을 보낼 때 호출됨
    // -> 요청 본문에는 친구 요청을 보낼 상대방의 ID가 포함됨
    @PostMapping("/{userId}/following")
    public ResponseEntity<Object> sendFollowRequest(@PathVariable Long userId,
                                                    @RequestBody FollowRequestDto followRequestDto) {
        // 친구 요청 서비스 호출
        followService.sendFollowRequest(followRequestDto, userId);

        // 친구 요청이 성공적으로 처리된 경우, 201 Created 상태를 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FollowResponseMessageDto("팔로우 요청이 완료되었습니다."));
    }

    // 친구 수락 (PATCH /followers/{userId}/accept)
    // -> 사용자가 친구 요청을 수락할 때 호출됨
    // -> 요청 본문에 친구 요청을 보낸 사용자의 ID가 포함됨
    @PatchMapping("/{userId}/accept")
    public ResponseEntity<Object> acceptFollowRequest(@PathVariable Long userId,
                                                      @RequestBody FollowRequestDto followRequestDto) {
        try {
            Long followingId = userId;                        // 수락하는 사람의 ID (현재 사용자의 ID)
            Long followerId = followRequestDto.getFollowId(); // 친구 요청을 보낸 사람의 ID

            // 친구 요청 수락 서비스 호출
            followService.acceptFollowRequest(followerId, followingId);

            // 친구 수락이 완료되면 200 OK 상태를 반환
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new FollowResponseMessageDto("친구 수락이 완료되었습니다."));

        } catch (IllegalArgumentException e) {
            // 친구 요청이 없는 경우 403 Forbidden 상태 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FollowResponseMessageDto("친구 요청이 없습니다."));
        } catch (Exception e) {
            // 서버 오류 발생 시 500 Internal Server Error 상태 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FollowResponseMessageDto("서버 오류가 발생했습니다."));
        }
    }

    // 친구 거절 (PATCH /followers/{userId}/reject)
    // -> 요청 본문에 친구 요청을 거절할 상대방의 ID가 포함됨
    @PatchMapping("/{userId}/reject")
    public ResponseEntity<Object> rejectFollowRequest(@PathVariable Long userId,
                                                      @RequestBody FollowRequestDto followRequestDto) {
        try {
            Long followId = followRequestDto.getFollowId();  // 친구 요청을 거절할 'followId'

            // 친구 요청이 대기 중인 상태에서는 관계를 삭제하는 것으로 처리
            followService.rejectFollowRequest(followId, userId);

            // 친구 요청 거절이 완료되면 200 OK 상태 반환
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new FollowResponseMessageDto("친구 요청이 거절되었습니다."));

        } catch (IllegalArgumentException e) {
            // 친구 요청이 이미 수락된 상태일 때 발생한 예외 처리
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FollowResponseMessageDto(e.getMessage()));  // 수정된 메시지 출력
        } catch (Exception e) {
            // 서버 오류 발생 시 500 Internal Server Error 상태 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FollowResponseMessageDto("서버 오류가 발생했습니다."));
        }
    }

    // 친구 삭제 (DELETE /followers/{userId})
    // -> 요청 본문에 삭제할 친구의 ID가 포함됨
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteFriend(@PathVariable Long userId,
                                               @RequestBody FollowRequestDto followRequestDto) {
        try {
            Long followerId = followRequestDto.getFollowId();
            // 친구 삭제 서비스 호출
            followService.deleteFriend(followerId, userId);

            // 친구 삭제가 완료되면 200 OK 상태 반환
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new FollowResponseMessageDto("친구 삭제가 완료되었습니다."));

        } catch (IllegalArgumentException e) {
            // 친구가 아닌 경우 403 Forbidden 상태 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FollowResponseMessageDto("친구가 아닙니다."));
        } catch (Exception e) {
            // 서버 오류 발생 시 500 Internal Server Error 상태 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FollowResponseMessageDto("서버 오류가 발생했습니다."));
        }
    }

    // 친구 리스트 조회 (GET /followers/{userId}/follows)
    // 친구 목록을 반환하며, 500 Internal Server Error 상태는 서버 오류 발생 시 반환됨
    @GetMapping("/{userId}/follows")
    public ResponseEntity<Object> getFriendList(@PathVariable Long userId) {
        try {
            // 친구 목록 조회 서비스 호출
            List<FollowListDto> friends = followService.getFriendList(userId);
            // 친구 목록을 반환하며 200 OK 상태 반환
            return ResponseEntity.status(HttpStatus.OK)
                    .body(friends);
        } catch (Exception e) {
            // 서버 오류 발생 시 500 Internal Server Error 상태 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FollowResponseMessageDto("서버 오류가 발생했습니다."));
        }
    }
}
