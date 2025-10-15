package com.example.bankcards.service;


import com.example.bankcards.dto.auth.RefreshToken;
import com.example.bankcards.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void testCreateRefreshToken() {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken("testuser");

        assertNotNull(refreshToken.getToken());
        assertEquals("testuser", refreshToken.getUsername());
        assertNotNull(refreshToken.getExpiryDate());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpirationValid() {
        RefreshToken token = new RefreshToken("testuser", "token", Instant.now().plusSeconds(3600));

        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(token);

        assertEquals(token, verifiedToken);
    }

    @Test
    void testVerifyExpirationExpired() {
        RefreshToken token = new RefreshToken("testuser", "token", Instant.now().minusSeconds(3600));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository).deleteByUserv("testuser");
    }

    @Test
    void testFindByToken() {
        RefreshToken token = new RefreshToken("testuser", "token", Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken("token");

        assertTrue(foundToken.isPresent());
        assertEquals("token", foundToken.get().getToken());

        verify(refreshTokenRepository).findByToken("token");
    }

    @Test
    void testFindByTokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken("invalid");

        assertFalse(foundToken.isPresent());

        verify(refreshTokenRepository).findByToken("invalid");
    }
}