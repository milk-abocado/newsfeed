package com.example.newsfeed.service;

import com.example.newsfeed.dto.PostFeedItemDto;
import com.example.newsfeed.dto.PostFeedResponseDto;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.repository.FollowRepository;
import com.example.newsfeed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostFeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    /**
     * 로그인한 사용자 ID로 뉴스피드 조회 (팔로우한 사용자 + 본인)
     */
    public PostFeedResponseDto getNewsfeed(Long userId, Pageable pageable) {
        List<Long> followingUserIds = followRepository.findFolloweeIdsByFollowerId(userId);
        followingUserIds.add(userId); // 본인 포함

        return getFeed(followingUserIds, pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * 사용자 ID 리스트로 뉴스피드 조회 (최신순 + 페이지네이션)
     */
    public PostFeedResponseDto getFeed(List<Long> followingUserIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Posts> postsPage = postRepository.findFeedByUserIds(followingUserIds, pageable);

        List<PostFeedItemDto> postDtos = postsPage.getContent().stream()
                .map(PostFeedItemDto::fromEntity)
                .collect(Collectors.toList());

        return new PostFeedResponseDto(
                postDtos,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages()
        );
    }
}
