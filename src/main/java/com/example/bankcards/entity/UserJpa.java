package com.example.bankcards.entity;

import com.example.bankcards.util.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Table(name = "user")
@Entity
@Data
public class UserJpa {
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
    @Column(name = "password", length = 100)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardJpa> cardJpa = new ArrayList<>();


    public UserJpa(String username, String firstName, String surName, String patronymic, String email, String telephone, String password, UserRole role) {
        this.username = username;
        this.firstName = firstName;
        this.surName = surName;
        this.patronymic = patronymic;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.role = role;
    }

    public UserJpa() {
        
    }
}
