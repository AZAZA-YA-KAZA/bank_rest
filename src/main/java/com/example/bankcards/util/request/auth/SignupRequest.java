package com.example.bankcards.util.request.auth;

public record SignupRequest(
        String username,
        String firstName,
        String surName,
        String patronymic,
        String email,
        String telephone,
        String password,
        String role) {
}
