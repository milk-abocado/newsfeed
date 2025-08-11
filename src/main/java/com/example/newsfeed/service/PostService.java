package com.example.newsfeed.service;

import com.example.newsfeed.dto.PostPageResponseDto;
import com.example.newsfeed.dto.PostRequestDto;
import com.example.newsfeed.dto.PostResponseDto;
import com.example.newsfeed.entity.PostImages;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.PostLikeRepository;
import com.example.newsfeed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    // 게시물 생성
    // @param requestDto 요청 데이터 (content, image URL 등)
    // @param user 게시물 작성자 (현재는 mock 유저)
    // @return 생성된 게시물의 응답 DTO
    public PostResponseDto createPost(PostRequestDto requestDto, Users user) {
        if (user.getNickname() == null) {
            user.setNickname("user");
        }

        // 1. 게시물 엔티티 생성
        Posts post = Posts.builder()
                .user(user)
                .content(requestDto.getContent())
                .build();

        // 2. 이미지가 존재할 경우, 이미지 엔티티 생성 후 게시물과 연결
        if (requestDto.getImageUrlList() != null) {
            requestDto.getImageUrlList().forEach(url -> {
                PostImages image = PostImages.builder()
                        .imageUrl(url)
                        .post(post)
                        .build();
                post.addImage(image);
            });
        }

        // 3. 게시물 저장 (cascade로 인해 이미지도 함께 저장됨)
        Posts savedPost = postRepository.save(post);

        // 4. 저장 직후 연관 데이터(postImageList)는 LAZY 로딩 상태일 수 있으므로 다시 조회
        Posts fullPost = postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new RuntimeException("게시물 저장 후 조회 실패"));

        // 5. 응답 DTO 생성 및 반환
        // 생성 직후 likeCount=0, likedByCurrentUser=false
        return PostResponseDto.builder()
                .id(fullPost.getId())
                .userId(fullPost.getUser().getId())
                .nickname(fullPost.getUser().getNickname())
                .content(fullPost.getContent())
                .imageUrlList(fullPost.getPostImageList().stream()
                        .map(PostImages::getImageUrl)
                        .collect(Collectors.toList()))
                .createdAt(fullPost.getCreatedAt())
                .updatedAt(fullPost.getUpdatedAt())
                .likeCount(0L)
                .likedByCurrentUser(false)
                .build();
    }

    // 게시물 단건 조회
    // @param postId 게시물 ID
    // @param currentUser 현재 로그인한 유저
    // @return 해당 게시물의 응답 DTO
    public PostResponseDto getPostById(Long postId, Users currentUser) {
        // 1. 사용자 인증 확인
        if (currentUser == null) {
            throw new IllegalArgumentException("401: 로그인하지 않은 사용자입니다.");
        }

        // 2. 게시물 존재 여부 확인
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("404: 게시물이 존재하지 않습니다."));

        long likeCount = postLikeRepository.countByPost_Id(post.getId());
        boolean likedByMe = postLikeRepository.existsByPost_IdAndUser_Id(post.getId(), currentUser.getId());

//        // 2-1. 게시물 본인 것만 확인 가능
//        if (!post.getUser().getId().equals(currentUser.getId())) {
//            throw new IllegalArgumentException("403: 접근 권한이 없습니다.");
//        }

        // 3. 응답 DTO 생성 및 반환
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .content(post.getContent())
                .imageUrlList(post.getPostImageList().stream()
                        .map(PostImages::getImageUrl)
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .likedByCurrentUser(likedByMe)
                .build();
    }

    // 게시물 전체 조회 (페이징 처리)
    // @param page 요청한 페이지 번호 (0부터 시작)
    // @param size 한 페이지에 조회할 게시물 개수
    // @param currentUser 현재 로그인한 유저
    // @return 페이징 처리된 게시물 리스트 DTO
    public PostPageResponseDto getAllPosts(int page, int size, Users currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("401: 로그인하지 않은 사용자입니다.");
        }

        // 1. 페이징 요청 설정 (최신순 정렬)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // 2. 페이징 조회
        Page<Posts> postsPage = postRepository.findAll(pageRequest);

        // 3. 각 게시물마다 DTO로 변환
        List<PostResponseDto> postDtosList = postsPage.getContent().stream()
                .map(post -> {
                    long likeCount = postLikeRepository.countByPost_Id(post.getId());
                    boolean likedByMe = postLikeRepository.existsByPost_IdAndUser_Id(post.getId(), currentUser.getId());

                    return PostResponseDto.builder()
                            .id(post.getId())
                            .userId(post.getUser().getId())
                            .nickname(post.getUser().getNickname())
                            .content(post.getContent())
                            .imageUrlList(post.getPostImageList().stream()
                                    .map(PostImages::getImageUrl)
                                    .collect(Collectors.toList()))
                            .createdAt(post.getCreatedAt())
                            .updatedAt(post.getUpdatedAt())
                            .likeCount(likeCount)
                            .likedByCurrentUser(likedByMe)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. 페이징 결과 포함한 DTO 반환
        return new PostPageResponseDto(
                postDtosList,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages()
        );
    }
}
