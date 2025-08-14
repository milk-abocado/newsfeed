package com.example.newsfeed.service.Comment;

import com.example.newsfeed.dto.Comment.CommentsPageResponseDto;
import com.example.newsfeed.dto.Comment.CommentsRequestDto;
import com.example.newsfeed.dto.Comment.CommentsResponseDto;
import com.example.newsfeed.entity.Comment.Comments;
import com.example.newsfeed.entity.Post.Posts;
import com.example.newsfeed.entity.User.Users;
import com.example.newsfeed.repository.Comment.CommentRepository;
import com.example.newsfeed.repository.Like.CommentLikeRepository;
import com.example.newsfeed.repository.Post.PostRepository;
import com.example.newsfeed.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    // 댓글 작성
    // @param postId        댓글이 달릴 게시물 ID
    // @param currentUserId 현재 로그인한 사용자 ID
    // @param req           댓글 작성 요청 DTO (내용 포함)
    // @return 작성된 댓글 정보 DTO
    public CommentsResponseDto create(Long postId, Long currentUserId, CommentsRequestDto req) {
        // 내용이 null 이거나 공백만 있을 경우 예외 처리
        String content = (req.getContent() == null) ? "" : req.getContent().trim();
        if (content.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 비어 있을 수 없습니다.");
        }

        // 댓글을 달 게시물 존재 여부 확인
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));

        // 댓글 작성자(사용자) 존재 여부 확인
        Users user = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 댓글 저장
        Comments saved = commentRepository.save(Comments.builder()
                .post(post)
                .user(user)
                .content(content)
                .build());

        // 저장된 댓글 DTO로 변환 후 반환
        return toDto(saved, currentUserId);
    }

    // 댓글 목록 조회 (페이지네이션)
    // @param postId 게시물 ID
    // @param page   페이지 번호
    // @param size   페이지 크기
    // @return 댓글 페이지 응답 DTO
    @Transactional(readOnly = true)
    public CommentsPageResponseDto getList(Long postId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 해당 게시물의 댓글을 최신순으로 조회
        Page<Comments> pageData = commentRepository.findByPost_IdOrderByCreatedAtDesc(postId, pageable);

        // Page 데이터를 DTO로 변환
        CommentsPageResponseDto res = new CommentsPageResponseDto();
        res.setContentList(pageData.getContent().stream()
                .map(c -> toDto(c, currentUserId))
                .collect(Collectors.toList()));
        res.setPage(pageData.getNumber());
        res.setSize(pageData.getSize());
        res.setTotalElements(pageData.getTotalElements());
        res.setTotalPages(pageData.getTotalPages());
        res.setLast(pageData.isLast());
        return res;
    }

    // 댓글 수정 (댓글 작성자만 가능)
    // @param commentId     수정할 댓글 ID
    // @param currentUserId 현재 로그인한 사용자 ID
    // @param req           수정할 내용 DTO
    // @return 수정된 댓글 정보 DTO
    public CommentsResponseDto update(Long commentId, Long currentUserId, CommentsRequestDto req) {
        // 내용 검증
        String content = (req.getContent() == null) ? "" : req.getContent().trim();
        if (content.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 비어 있을 수 없습니다.");
        }

        // 댓글 존재 여부 확인
        Comments c = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 현재 로그인한 사용자가 댓글 작성자인지 확인
        Long commentAuthorId = c.getUser().getId();
        if (!currentUserId.equals(commentAuthorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정할 수 있습니다.");
        }

        // 내용 수정 (영속성 컨텍스트 적용)
        c.setContent(content); // 영속 상태 -> 커밋 시 업데이트
        return toDto(c, currentUserId);
    }

    // 댓글 삭제 (댓글 작성자 또는 게시글 작성자 가능)
    // @param commentId     삭제할 댓글 ID
    // @param currentUserId 현재 로그인한 사용자 ID
    public void delete(Long commentId, Long currentUserId) {
        // 댓글 존재 여부 확인
        Comments c = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 댓글 작성자와 게시물 작성자 ID 확인
        Long commentAuthorId = c.getUser().getId();
        Long postAuthorId = c.getPost().getUser().getId();

        // 권한 체크: 댓글 작성자 또는 게시글 작성자만 가능
        if (!currentUserId.equals(commentAuthorId) && !currentUserId.equals(postAuthorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(c);
    }

    // 엔티티를 DTO로 변환
    private CommentsResponseDto toDto(Comments c, Long currentUserId) {
        long likeCount = commentLikeRepository.countByComment_Id(c.getId());
        boolean likedByMe = (currentUserId != null) &&
                commentLikeRepository.existsByComment_IdAndUser_Id(c.getId(), currentUserId);

        return CommentsResponseDto.builder()
                .id(c.getId())
                .postId(c.getPost().getId())
                .userId(c.getUser().getId())
                .nickname(c.getUser().getNickname())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .likeCount(likeCount)
                .likedByCurrentUser(likedByMe)
                .build();
    }
}
