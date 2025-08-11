package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_comment_like_user_comment", columnNames = {"user_id", "comment_id"}))
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 고유 Id

    // 좋아요를 누른 사용자 (N:1 관계)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // 좋아요 대상 댓글 (N:1 관계)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comments comment;
}
