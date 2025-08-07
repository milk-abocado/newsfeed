package com.example.newsfeed.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = :"is_deleted = false")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String email;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void softDelete() {
    this.isDeleted = true;
    }
}
