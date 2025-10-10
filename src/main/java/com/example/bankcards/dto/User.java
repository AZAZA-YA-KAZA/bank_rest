package com.example.bankcards.dto;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "user")
@Entity
@Data
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Column(name = "username", length = 50, unique = true)
    private String username;
    @Column(name = "first_name", length = 100)
    private String firstName;
    @Column(name = "sur_name", length = 100)
    private String surName;
    @Column(name = "patronymic", length = 100)
    private String patronymic;
    @Column(name = "email", length = 50, unique = true)
    private String email;
    @Column(name = "telephone", length = 50, unique = true)
    private String telephone;
    @Column(name = "password", length = 50)
    private String password;
    @Column(name = "role", length = 50)
    private String role;
    @Column(name = "create_at", length = 50)
    private String createAt;

}
