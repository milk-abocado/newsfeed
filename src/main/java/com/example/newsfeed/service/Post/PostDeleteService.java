package com.example.newsfeed.service.Post;

import com.example.newsfeed.entity.Post.Posts;
import com.example.newsfeed.repository.Post.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostDeleteService {

    // 게시물 데이터를 DB에서 조회/저장/삭제하는 JPA 인터페이스
    private final PostRepository postRepository;

    // 트랜잭션 : 이 메서드에서 예외 발생 시 모든 변경 롤백
    @Transactional
    public void deletePost(Long postId, Long userId) {

        // postId에 해당하는 게시물을 DB에서 조회 (없으면 404 예외 발생)
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시물이 존재하지 않습니다."));

        // 현재 로그인한 userId가 이 게시물의 작성자가 아닌 경우 403 예외 발생
        if (!post.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 게시물만 삭제할 수 있습니다.");
        }

        // hard delete -> DB에서 제거
        postRepository.delete(post);
    }
}
