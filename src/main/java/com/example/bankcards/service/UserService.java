package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.util.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private  final PasswordEncoder passwordEncoder;
    public UserService(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO updateUser(Long userId, String username, String firstName, String surName, String patronymic, String email, String telephone, String password, String role) {
        // Проверка уникальности
        if (userJpaRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        if (userJpaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже занят");
        }
        if (userJpaRepository.existsByTelephone(telephone)) {
            throw new IllegalArgumentException("Телефон уже занят");
        }
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        userJpaRepository.updateById(userId,
                username,
                firstName,
                surName,
                patronymic,
                email,
                telephone,
                passwordEncoder.encode(password),
                userRole);
        return new UserDTO(
                username,
                firstName,
                surName,
                patronymic,
                email,
                telephone
        );
    }

    public UserJpa getById(Long userId) {
        return userJpaRepository.getById(userId);

    }

    public String deleteUser(Long userId) {
        userJpaRepository.deleteById(userId);
        return "Ok";
    }
}
