package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.security.JwtResponse;
import com.example.bankcards.util.request.auth.LoginRequest;
import com.example.bankcards.util.request.auth.SignupRequest;
import com.example.bankcards.util.request.auth.TokenRefreshRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthService {
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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
    }

    public Object refreshToken(TokenRefreshRequest request) {
    }
}
