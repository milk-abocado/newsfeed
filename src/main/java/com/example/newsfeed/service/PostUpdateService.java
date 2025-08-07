package com.example.newsfeed.service;

import com.example.newsfeed.dto.PostUpdateRequestDto;
import com.example.newsfeed.entity.PostImages;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.repository.PostImageRepository;
import com.example.newsfeed.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service                          // 서비스 계층 클래스임을 스프링에게 알림
@RequiredArgsConstructor          // 생성자 자동 생성 (final 필드만)
@Transactional                   // 전체 작업을 하나의 트랜잭션으로 처리 (실패 시 rollback)
public class PostUpdateService {

    // 게시물 DB 접근용 Repository
    private final PostRepository postRepository;

    // 게시물 이미지 DB 접근용 Repository
    private final PostImageRepository postImageRepository;

    /**
     * 게시물 수정 메서드
     * @param postId 수정할 게시물의 ID
     * @param requestDto 수정할 내용 (본문, 이미지 추가/삭제 정보)
     * @param userId 현재 로그인한 사용자 ID
     * @return 수정된 게시물 객체
     */
    public Posts updatePost(Long postId, PostUpdateRequestDto requestDto, Long userId) {

        // 1. 게시물 조회 (없으면 404 예외)
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        // 2. 권한 체크: 작성자와 현재 로그인 유저가 같은지 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("게시물을 수정할 권한이 없습니다.");
        }

        // 3. 이미지 삭제 처리
        if (requestDto.getDeleteImageId() != null) {
            for (Long imageId : requestDto.getDeleteImageId()) {
                // 이미지 ID로 이미지 조회
                Optional<PostImages> image = postImageRepository.findById(imageId);

                image.ifPresent(img -> {
                    // 해당 이미지가 현재 게시물에 속한 이미지인지 확인
                    if (img.getPost().getId().equals(postId)) {
                        // 1) 게시물에서 이미지 제거 (연관관계 삭제)
                        post.removeImage(img);
                        // 2) 실제 DB에서 이미지 삭제
                        postImageRepository.delete(img);
                    }
                });
            }
        }

        // 4. 이미지 추가 처리
        if (requestDto.getAddImageUrl() != null) {
            int currentImageCount = post.getImages().size();              // 현재 게시물 이미지 수
            int newImageCount = requestDto.getAddImageUrl().size();      // 새로 추가할 이미지 수

            // 총합이 3개 초과되면 예외 발생
            if (currentImageCount + newImageCount > 3) {
                throw new IllegalArgumentException("이미지는 최대 3개까지만 등록할 수 있습니다.");
            }

            // 각 이미지 URL에 대해 PostImages 객체 생성 후 게시물에 추가
            for (String imageUrl : requestDto.getAddImageUrl()) {
                PostImages newImage = PostImages.builder()
                        .imageUrl(imageUrl)
                        .post(post)
                        .build();

                post.addImage(newImage);  // 연관관계 설정
            }
        }

        // 5. 게시물 본문 수정
        post.updateContent(requestDto.getContent());

        // 6. 게시물 + 이미지 최종 저장 (Cascade -> 이미지도 같이 저장됨)
        return postRepository.save(post);
    }
}
