package com.example.bankcards.util.request;

public record UserRequest(
        String username,
        String firstName,
        String surName,
        String patronymic,
        String email,
        String telephone,
        String password,
        String role
) {
}
