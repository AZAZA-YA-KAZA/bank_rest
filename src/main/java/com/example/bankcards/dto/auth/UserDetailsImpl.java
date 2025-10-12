package com.example.bankcards.dto.auth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final String username;

    private final Long userId;

    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, Long userId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.userId = userId;
        this.password = password;
        this.authorities = authorities;
    }
}
