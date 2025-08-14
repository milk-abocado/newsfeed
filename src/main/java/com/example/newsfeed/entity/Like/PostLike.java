package com.example.newsfeed.entity.Like;

import com.example.newsfeed.entity.Post.Posts;
import com.example.newsfeed.entity.User.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_like_user_post", columnNames = {"user_id", "post_id"}))
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 고유 Id

    // 좋아요를 누른 사용자 (N:1 관계)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // 좋아요 대상 게시물 (N:1 관계)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;
}
