package com.example.bankcards.dto.auth;

import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.util.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Data
public class UserDetailsImpl implements UserDetails {
    private Long userId;
    private String username;
    private String password;
    private UserRole role;

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(UserJpa user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}