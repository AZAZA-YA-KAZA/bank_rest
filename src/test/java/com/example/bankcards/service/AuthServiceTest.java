package com.example.bankcards.service;


import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.dto.auth.RefreshToken;
import com.example.bankcards.dto.auth.UserDetailsImpl;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.security.JwtResponse;
import com.example.bankcards.security.JwtUtils;
import com.example.bankcards.util.request.auth.LoginRequest;
import com.example.bankcards.util.request.auth.SignupRequest;
import com.example.bankcards.util.request.auth.TokenRefreshRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private TokenRefreshRequest refreshRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("Doe", "password");
        signupRequest = new SignupRequest("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "password", "USER");
        refreshRequest = new TokenRefreshRequest("refresh-token");
    }

    @Test
    void testAuthenticateUser() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUsername("testuser");
        userDetails.setUserId(1L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(new RefreshToken("testuser", "refresh-token", null));

        JwtResponse response = authService.authenticateUser(loginRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getSurname());
        assertEquals(1L, response.getUserId());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegisterUser() {
        when(userJpaRepository.existsByUsername(anyString())).thenReturn(false);
        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);
        when(userJpaRepository.existsByTelephone(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(userJpaRepository.save(any(UserJpa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO userDTO = authService.registerUser(signupRequest);

        assertEquals("testuser", userDTO.getUsername());
        assertEquals("John", userDTO.getFirstName());

        verify(userJpaRepository).save(any(UserJpa.class));
    }

    @Test
    void testRegisterUserDuplicateUsername() {
        when(userJpaRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(signupRequest));

        verify(userJpaRepository, never()).save(any(UserJpa.class));
    }

    @Test
    void testRefreshToken() {
        RefreshToken refreshToken = new RefreshToken("testuser", "refresh-token", null);
        when(refreshTokenService.findByToken("refresh-token")).thenReturn(java.util.Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtils.generateTokenFromUsername("testuser")).thenReturn("new-jwt");
        UserDTO user = new UserDTO();
        user.setUserId(1L);
        when(userJpaRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        JwtResponse response = (JwtResponse) authService.refreshToken(refreshRequest);

        assertEquals("new-jwt", response.getToken());
        assertEquals("testuser", response.getSurname());
        assertEquals(1L, response.getUserId());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(refreshTokenService).findByToken("refresh-token");
    }

    @Test
    void testRefreshTokenInvalid() {
        when(refreshTokenService.findByToken("invalid-token")).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.refreshToken(new TokenRefreshRequest("invalid-token")));

        verify(refreshTokenService).findByToken("invalid-token");
    }
}