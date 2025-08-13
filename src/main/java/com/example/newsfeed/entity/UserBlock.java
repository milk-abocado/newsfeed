package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_block",
        uniqueConstraints =  { @UniqueConstraint(name = "uq_block", columnNames = {"user_id", "target_user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //차단한 사람
    @Column(name = "user_id")
    private Long userId;
    //차단당한 사람
    @Column (name = "target_user_id")
    private Long targetUserId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
