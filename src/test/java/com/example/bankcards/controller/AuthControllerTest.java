package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.security.JwtResponse;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.util.request.auth.LoginRequest;
import com.example.bankcards.util.request.auth.SignupRequest;
import com.example.bankcards.util.request.auth.TokenRefreshRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testAuthenticateUser() throws Exception {
        JwtResponse response = new JwtResponse("jwt-token", "testuser", 1L, "refresh-token");
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void testAuthenticateUserInvalid() throws Exception {
        doThrow(new IllegalArgumentException("Invalid credentials")).when(authService).authenticateUser(any(LoginRequest.class));

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void testRegisterUser() throws Exception {
        UserDTO userDTO = new UserDTO("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890");
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(authService).registerUser(any(SignupRequest.class));
    }

    @Test
    void testRegisterUserDuplicate() throws Exception {
        doThrow(new IllegalArgumentException("Имя пользователя уже занято")).when(authService).registerUser(any(SignupRequest.class));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(authService).registerUser(any(SignupRequest.class));
    }

    @Test
    void testRefreshToken() throws Exception {
        JwtResponse response = new JwtResponse("new-jwt", "testuser", 1L, "refresh-token");
        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-jwt"));

        verify(authService).refreshToken(any(TokenRefreshRequest.class));
    }

    @Test
    void testRefreshTokenInvalid() throws Exception {
        doThrow(new RuntimeException("Refresh token not found!")).when(authService).refreshToken(any(TokenRefreshRequest.class));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is5xxServerError());

        verify(authService).refreshToken(any(TokenRefreshRequest.class));
    }
}