package com.example.bankcards.util.request.auth;


import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String surname,
        @NotBlank
        String password
) {
}
