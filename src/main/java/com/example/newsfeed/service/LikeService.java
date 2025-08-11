package com.example.newsfeed.service;

import com.example.newsfeed.dto.LikeStatusResponseDto;
import com.example.newsfeed.entity.Comments;
import com.example.newsfeed.entity.CommentLike;
import com.example.newsfeed.entity.PostLike;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.CommentLikeRepository;
import com.example.newsfeed.repository.CommentRepository;
import com.example.newsfeed.repository.PostLikeRepository;
import com.example.newsfeed.repository.PostRepository;
import com.example.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    // 게시물 좋아요
    // @param postId        게시물 ID
    // @param currentUserId 현재 로그인 사용자 ID
    // @return 좋아요 상태 및 개수를 담은 DTO
    public LikeStatusResponseDto likePost(Long postId, Long currentUserId) {
        // 게시물 조회
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));

        // 사용자 조회
        Users user = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 자기 자신의 게시물에는 좋아요 불가
        if (post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신의 게시물에는 좋아요를 누를 수 없습니다.");
        }

        // 중복 좋아요 방지
        if (postLikeRepository.existsByPost_IdAndUser_Id(postId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시물입니다.");
        }

        // 좋아요 저장
        postLikeRepository.save(PostLike.builder().post(post).user(user).build());

        // 현재 좋아요 개수 조회
        long count = postLikeRepository.countByPost_Id(postId);

        return LikeStatusResponseDto.builder()
                .targetId(postId)
                .liked(true)
                .likeCount(count)
                .targetType("POST")
                .build();
    }

    // 게시물 좋아요 취소
    // @param postId        게시물 ID
    // @param currentUserId 현재 로그인 사용자 ID
    // @return 좋아요 상태 및 개수를 담은 DTO
    public LikeStatusResponseDto unlikePost(Long postId, Long currentUserId) {
        // 좋아요 엔티티 조회
        PostLike like = postLikeRepository.findByPost_IdAndUser_Id(postId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "좋아요가 존재하지 않습니다."));

        // 좋아요 삭제
        postLikeRepository.delete(like);

        // 현재 좋아요 개수 조회
        long count = postLikeRepository.countByPost_Id(postId);

        return LikeStatusResponseDto.builder()
                .targetId(postId)
                .liked(false)
                .likeCount(count)
                .targetType("POST")
                .build();
    }

    // 댓글 좋아요
    // @param commentId     댓글 ID
    // @param currentUserId 현재 로그인 사용자 ID
    // @return 좋아요 상태 및 개수를 담은 DTO
    public LikeStatusResponseDto likeComment(Long commentId, Long currentUserId) {
        // 댓글 조회
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 사용자 조회
        Users user = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 자기 자신의 댓글에는 좋아요 불가
        if (comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신의 댓글에는 좋아요를 누를 수 없습니다.");
        }

        // 중복 좋아요 방지
        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 좋아요를 누른 댓글입니다.");
        }

        // 좋아요 저장
        commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());

        // 현재 좋아요 개수 조회
        long count = commentLikeRepository.countByComment_Id(commentId);

        return LikeStatusResponseDto.builder()
                .targetId(commentId)
                .liked(true)
                .likeCount(count)
                .targetType("COMMENT")
                .build();
    }

    // 댓글 좋아요 취소
    // @param commentId     댓글 ID
    // @param currentUserId 현재 로그인 사용자 ID
    // @return 좋아요 상태 및 개수를 담은 DTO
    public LikeStatusResponseDto unlikeComment(Long commentId, Long currentUserId) {
        // 좋아요 삭제 (삭제된 행 개수 반환)
        long deleted = commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, currentUserId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "좋아요가 존재하지 않습니다.");
        }

        // 현재 좋아요 개수 조회
        long count = commentLikeRepository.countByComment_Id(commentId);

        return LikeStatusResponseDto.builder()
                .targetId(commentId)
                .liked(false)
                .likeCount(count)
                .targetType("COMMENT")
                .build();
    }

}
