package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Posts {

    @Id // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL의 AUTO_INCREMENT 방식
    private Long id;

    // 게시물 작성자 (users 테이블 참조, N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)              // 지연 로딩: 접근 시점에 유저 정보 조회
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 컬럼명 지정
    private Users user;

    // 게시물 내용 (길이 제한 없는 TEXT 컬럼)
    @Column(columnDefinition = "TEXT")
    private String content;

    // 게시물에 연결된 이미지들
    // 게시물에 연결된 이미지들 (1:N 관계, 양방향 매핑)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImages> postImageList = new ArrayList<>();

    // 생성 시각 (자동 생성)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 수정 시각 (업데이트 시 자동 변경)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 삭제 여부 (소프트 삭제 구현 시 사용 가능)
    //private Boolean isDeleted = false;
    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    // 편의 메서드

    // 게시물 내용 수정 메서드 (updatedAt은 수동으로 갱신함)
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 게시물에 이미지 추가하는 메서드
    // postImageList에 추가
    // 해당 이미지의 post 참조도 this로 설정
    public void addImage(PostImages image) {
        this.postImageList.add(image);
        image.setPost(this);
    }

    // 게시물에서 이미지를 제거하는 메서드
    // 리스트에서 제거
    // image의 post 참조도 null로 해제
    public void removeImage(PostImages image) {
        this.postImageList.remove(image);
        image.setPost(null);
    }
}
