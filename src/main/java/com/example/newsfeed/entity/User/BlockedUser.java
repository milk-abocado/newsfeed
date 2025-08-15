package com.example.newsfeed.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blocked_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //차단한 사람
    @Column(name = "user_id")
    private Long userId;

    //차단당한 사람
    @Column(name = "target_user_id")
    private Long targetUserId;
}
