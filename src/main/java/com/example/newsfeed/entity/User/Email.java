package com.example.newsfeed.entity.User;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String verificationToken;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Column(nullable = false)
    private boolean isVerified = false;

}
