package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.engine.spi.Status;

import java.time.LocalDateTime;


@Entity
@Table(name = "users") //DB 테이블명
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String name;


    @Column(length = 100)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(length = 100)
    private String hometown;

    @Column(length = 100)
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

    // 프로필 수정 기능
//    public void update(String nickname, String bio, String school, String profileImage) {
//        this.nickname = nickname;
//        this.bio = bio;
//        this.school = school;
//       this.profileImage = profileImage;
//        this.updatedAt = LocalDateTime.now();
//    }

    public void softDelete() {
    this.isDeleted = true;
    }
}
