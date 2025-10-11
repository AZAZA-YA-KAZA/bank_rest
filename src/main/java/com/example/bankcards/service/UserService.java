package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.util.UserRole;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;

    public UserService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    public UserDTO createUser(String username, String firstName, String surName, String patronymic, String email, String telephone, String password, String role) {
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        // Создание объекта UserJpa с зашифрованным паролем
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

        // Сохранение пользователя в базе данных
        UserJpa savedUser = userJpaRepository.save(userJpa);

        // Создание и возврат UserDTO
        return new UserDTO(
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getSurName(),
                savedUser.getPatronymic(),
                savedUser.getEmail(),
                savedUser.getTelephone(),
                null, // Не возвращаем пароль в DTO
                savedUser.getRole()
        );
    }
}
