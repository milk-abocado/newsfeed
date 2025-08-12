package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 특정 사용자가 특정 게시물을 숨김 처리했음을 나타내는 엔티티
 * - 유저와 게시물 간의 숨김 관계 저장
 * - (user_id, post_id) 조합에 유니크 제약을 걸어 중복 숨김 방지
 * - 숨김 처리 일시(created_at) 기록
 */

@Entity
@Table(
        name = "hidden_posts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_hidden_user_post",             // 유니크 제약 이름
                columnNames = {"user_id", "post_id"}      // 유저와 게시물의 조합이 유일해야 함
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보호
public class HiddenPost {

    //PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //숨김 요청한 사용자
    @ManyToOne(fetch = FetchType.LAZY)                   // 필요 시에만 로딩(성능 최적화)
    @JoinColumn(name = "user_id", nullable = false)       // FK: users.id
    private Users user;

    //숨김 처리할 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)       // FK: posts.id
    private Posts post;

    // 숨김 처리 일시 (생성 시 자동 기록)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 숨김 엔티티 생성자
     * @param user 숨김을 요청한 사용자
     * @param post 숨김 대상 게시물
     */

    public HiddenPost(Users user, Posts post) {
        this.user = user;
        this.post = post;
    }

    // 엔티티가 처음 persist 되기 전에 createdAt 필드에 현재 시각 자동 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}