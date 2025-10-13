package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.dto.auth.RefreshToken;
import com.example.bankcards.dto.auth.UserDetailsImpl;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.security.JwtResponse;
import com.example.bankcards.security.JwtUtils;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.request.auth.LoginRequest;
import com.example.bankcards.util.request.auth.SignupRequest;
import com.example.bankcards.util.request.auth.TokenRefreshRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.surname(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new JwtResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getUserId(),
                refreshToken.getToken()
        );
    }

    public UserDTO registerUser(SignupRequest signUpRequest) {
        // Проверка уникальности
        if (userJpaRepository.existsByUsername(signUpRequest.username())) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        if (userJpaRepository.existsByEmail(signUpRequest.email())) {
            throw new IllegalArgumentException("Email уже занят");
        }
        if (userJpaRepository.existsByTelephone(signUpRequest.telephone())) {
            throw new IllegalArgumentException("Телефон уже занят");
        }
        String hashedPassword = passwordEncoder.encode(signUpRequest.password());
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(signUpRequest.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + signUpRequest.role());
        }
        UserJpa userJpa = new UserJpa(signUpRequest.username(),
                signUpRequest.firstName(),
                signUpRequest.surName(),
                signUpRequest.patronymic(),
                signUpRequest.email(),
                signUpRequest.telephone(),
                hashedPassword,
                userRole);
        userJpaRepository.save(userJpa);
        return new UserDTO(signUpRequest.username(),
                signUpRequest.firstName(),
                signUpRequest.surName(),
                signUpRequest.patronymic(),
                signUpRequest.email(),
                signUpRequest.telephone());
    }

    public Object refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsername)
                .map(username -> {
                    String token = jwtUtils.generateTokenFromUsername(username);
                    var user = userJpaRepository.findByUsername(username).orElseThrow();

                    return new JwtResponse(
                            token,
                            username,
                            user.getUserId(),
                            requestRefreshToken
                    );
                })
                .orElseThrow(() -> new RuntimeException(requestRefreshToken + "Refresh token not found!"));
    }
    }
