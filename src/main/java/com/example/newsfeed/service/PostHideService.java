package com.example.newsfeed.service;

import com.example.newsfeed.dto.PostHideResponseDto;   // 없으면 다음 단계에서 만들자
import com.example.newsfeed.entity.PostHide;
import com.example.newsfeed.entity.Posts;
import com.example.newsfeed.entity.Users;
import com.example.newsfeed.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostHideService {

    private final PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    // ISO-8601 응답 포맷 (예: 2025-08-11T13:05:23Z)
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 로그인한 '나' (myId) 기준으로, 내가 팔로우(수락됨)한 사용자의 'postId'를 숨김
     * - 게시물이 없거나 삭제됨 → 404
     * - 내 게시물 → 404
     * - 팔로우(수락)하지 않음 → 404
     * - 이미 숨김 → 200(멱등), 기존 hiddenAt 유지
     */

    public PostHideResponseDto hidePost(Long myId, Long postId) {
        // 1) 게시물 존재/삭제 여부
        Posts post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(this::notFound);

        Long authorId = post.getUser().getId();

        // 2) 내 글 차단 불가 -> 404
        if (authorId.equals(myId)) {
            throw selfHideNotAllowed();
        }

        // 4) 이미 숨김이면 멱등 응답(기존 createdAt 사용)
        PostHide existing = findHiddenPost(myId, postId);
        if (existing != null) { // 이미 있을 경우
            return new PostHideResponseDto(
                    postId,
                    true,
                    existing.getCreatedAt().atOffset(ZoneOffset.UTC).format(ISO)
            );
        }

        // 5) 새 숨김 저장
        Users meRef = em.getReference(Users.class, myId);
        PostHide saved = new PostHide(meRef, post);
        em.persist(saved); // @PrePersist로 createdAt 자동 세팅

        return new PostHideResponseDto(
                postId,
                true,
                saved.getCreatedAt().atOffset(ZoneOffset.UTC).format(ISO)
        );
    }

    // 게시물 숨김 기록 조회
    private PostHide findHiddenPost(Long meId, Long postId) {
        List<PostHide> list = em.createQuery("""
                select h from PostHide h
                where h.user.id = :uid and h.post.id = :pid
                """, PostHide.class)
                .setParameter("uid", meId)
                .setParameter("pid", postId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }


    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 삭제된 게시물입니다.");
    }

    private ResponseStatusException selfHideNotAllowed() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "본인 게시물은 차단할 수 없습니다.");
    }

}
