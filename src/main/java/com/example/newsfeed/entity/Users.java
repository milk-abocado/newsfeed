//package com.example.newsfeed.entity;
//
//public class Users {
//}

// 임시로 아래와 같이 최소 구조로만 Users 엔티티 만들어서 사용할 수 있음
package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
}
