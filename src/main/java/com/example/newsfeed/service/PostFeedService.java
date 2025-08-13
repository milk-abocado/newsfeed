package com.example.newsfeed.service;

import com.example.newsfeed.dto.PostFeedItemDto;
import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.entity.Follows;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.repository.FollowsRepository;
import com.example.newsfeed.repository.PostLikeRepository;
import com.example.newsfeed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostFeedService {

    private final PostRepository postRepository;
    private final FollowsRepository followRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 팔로잉 뉴스피드 조회
     * @param userId 로그인 사용자
     * @param pageable page,size (정렬은 내부에서 처리)
     * @param sort "updated"(기본, 수정일 최신) | "likes"(좋아요 많은 순)
     * @param start 작성일 시작(포함) "yyyy-MM-dd" 또는 "yyyy.MM.dd"
     * @param end   작성일 끝(포함)   "yyyy-MM-dd" 또는 "yyyy.MM.dd"
     */
    public PostFeedResponseDto getNewsfeed(Long userId, Pageable pageable, String sort, String start, String end) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

        // 1) 팔로잉 + 본인 ID 목록 조회
        List<Long> followingUserIds = followRepository.findByFollowerIdAndStatus(userId, true)
                .stream()
                .map(Follows::getFollowingId)
                .collect(Collectors.toList());
        followingUserIds.add(userId);

        // 2) 기간 파싱 (end는 +1일 00:00으로 만들어 [start, endExclusive) 범위로 검색)
        LocalDateTime startDt = parseDateNullable(start, true);
        LocalDateTime endExclusive = parseDateNullable(end, false);

        // 3) 정렬에 따라 다른 쿼리 호출
        Page<Posts> postsPage;
        if ("likes".equalsIgnoreCase(sort)) {
            // 좋아요 많은 순은 JPQL에서 ORDER BY 처리하므로 Pageable은 정렬 없이 전달
            Pageable noSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            postsPage = postRepository.findFeedExcludingHiddenByUserIdsAndDateRangeOrderByLikeCountDesc(
                    userId,                 // 게시물 차단 기능으로 인해 추가
                    followingUserIds,
                    startDt,
                    endExclusive,
                    noSort
            );
        } else {
            // 기본: 수정일 최신순
            Pageable updatedSort = PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );
            postsPage = postRepository.findFeedExcludingHiddenByUserIdsAndDateRange(
                    userId,                 // 게시물 차단 기능으로 인해 추가
                    followingUserIds,
                    startDt,
                    endExclusive,
                    updatedSort
            );
        }

        // 4) DTO 매핑 (좋아요 개수/내가 눌렀는지 포함)
        List<PostFeedItemDto> postDtos = postsPage.getContent().stream()
                .map(p -> {
                    long likeCount = postLikeRepository.countByPost_Id(p.getId());
                    boolean likedByMe = postLikeRepository.existsByPost_IdAndUser_Id(p.getId(), userId);

                    return PostFeedItemDto.builder()
                            .id(p.getId())
                            .userId(p.getUser().getId())
                            .nickname(p.getUser().getNickname())
                            .content(p.getContent())
                            .imageUrls(p.getPostImageList().stream()
                                    .map(img -> img.getImageUrl())
                                    .collect(Collectors.toList()))
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .likeCount(likeCount)
                            .likedByCurrentUser(likedByMe)
                            .build();
                })
                .collect(Collectors.toList());

        // 5) 페이지 응답
        return new PostFeedResponseDto(
                postDtos,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages()
        );
    }

    // ===== Helpers =====

    private LocalDateTime parseDateNullable(String s, boolean isStart) {
        if (s == null || s.isBlank()) return null;
        List<DateTimeFormatter> fmts = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd")
        );
        LocalDate d = null;
        for (DateTimeFormatter f : fmts) {
            try {
                d = LocalDate.parse(s.trim(), f);
                break;
            } catch (Exception ignore) {}
        }
        if (d == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 날짜 형식: " + s);

        return isStart ? d.atStartOfDay() : d.plusDays(1).atStartOfDay(); // [start, end+1일00:00)
    }
}
