package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.engine.spi.Status;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true, length = 255)
    private String password;

    @Column(nullable = true, length = 100)
    private String name;
  
    @Column(nullable = true, length = 100)
    private String nickname;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = true, name = "profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(nullable = true, length = 100)
    private String hometown;

    @Column(nullable = true, length = 100)
    private String school;

    @Column(name = "security_question", length = 255)
    private String securityQuestion;

    @Column(name = "security_answer", length = 255)
    private String securityAnswer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // 생성자
    public Users(String email, String password, String name, String nickname,
                 String securityQuestion, String securityAnswer, String profileImage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.profileImage = profileImage;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void softDelete() {
    this.isDeleted = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
  
}
