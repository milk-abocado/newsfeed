package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImages {

    @Id // 기본 키(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략 (MySQL의 AUTO_INCREMENT)
    private Long id;

    // 게시물(Post)과의 다대일(N:1) 관계 설정
    // 이미지가 연결된 게시물 (posts 테이블 참조)
    @ManyToOne(fetch = FetchType.LAZY)              // 지연 로딩: 실제 사용 시점에 조회
    @JoinColumn(name = "post_id", nullable = false) // 외래 키 컬럼명 지정 (posts 테이블의 id 참조)
    private Posts post;

    // 이미지 URL (TEXT 타입으로 저장)
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
}
