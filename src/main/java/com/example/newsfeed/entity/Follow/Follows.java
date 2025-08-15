package com.example.newsfeed.entity.Follow;

import com.example.newsfeed.entity.User.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(FollowsId.class)
public class Follows {
    @Id
    @Column(name = "follower_id")  // 컬럼 이름을 명시적으로 지정
    private Long followerId;       // 팔로우하는 사람의 ID

    @Id
    @Column(name = "following_id")  // 컬럼 이름을 명시적으로 지정
    private Long followingId;       // 팔로우되는 사람의 ID

    @ManyToOne
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private Users follower;    // 팔로우하는 사용자 엔티티

    @ManyToOne
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    private Users following;   // 팔로우되는 사용자 엔티티

    private Boolean status;     // 친구 요청 상태(TRUE: 수락, FALSE: 대기)
}
