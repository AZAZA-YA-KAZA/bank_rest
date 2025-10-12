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

    //доработать
    public UserJpa updateUser(String username, String firstName, String surName, String patronymic, String email, String telephone, String password, String role) {
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        UserDTO userDTO = new UserDTO(
                username,
                firstName,
                surName,
                patronymic,
                email,
                telephone
        );

        UserJpa userJpa = new UserJpa(
                username,
                firstName,
                surName,
                patronymic,
                email,
                telephone,
                passwordEncoder.encode(password), // Шифрование пароля
                userRole
        );
        return userJpaRepository.save(userJpa);
    }

    public UserJpa getById(Long userId) {
        return userJpaRepository.getById(Math.toIntExact(userId));

    }
}
