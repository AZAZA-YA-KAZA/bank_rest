package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "user_refresh_tokens")
@Entity
@Data
public class RefreshTokenJpa {
    @Id
    @Column(name = "refresh_token", length = 1024)
    private String refreshToken;
    @Column(name = "username", length = 255, nullable = false)
    private String username;
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public RefreshTokenJpa(String refreshToken, String username, LocalDateTime expiryDate) {
        this.refreshToken = refreshToken;
        this.username = username;
        this.expiryDate = expiryDate;
    }

    public RefreshTokenJpa() {
    }
}
