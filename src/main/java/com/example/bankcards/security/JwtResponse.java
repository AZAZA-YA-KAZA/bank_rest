package com.example.bankcards.security;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String surname;
    private Long userId;
    private String refreshToken;

    public JwtResponse(String token, String username, Long userId, String refreshToken) {
        this.token = token;
        this.surname = username;
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
