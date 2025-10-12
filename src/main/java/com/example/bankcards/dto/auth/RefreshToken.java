package com.example.bankcards.dto.auth;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {
    private String username;

    private String token;

    private Instant expiryDate;

    public RefreshToken(String username, String token, Instant expiryDate) {
        this.username = username;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
