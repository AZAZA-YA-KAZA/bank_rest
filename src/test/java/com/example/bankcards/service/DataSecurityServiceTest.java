package com.example.bankcards.service;


import com.example.bankcards.dto.auth.UserDetailsImpl;
import com.example.bankcards.entity.CardJpa;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.CardJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataSecurityServiceTest {

    @Mock
    private CardJpaRepository cardRepository;

    @InjectMocks
    private DataSecurityService dataSecurityService;

    @Test
    void testIsOwnerTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(new UserJpa());
        userDetails.setUserId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean isOwner = dataSecurityService.isOwner(1L);
        assertTrue(isOwner);
    }

    @Test
    void testIsOwnerFalse() {
        UserDetailsImpl userDetails = new UserDetailsImpl(new UserJpa());
        userDetails.setUserId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean isOwner = dataSecurityService.isOwner(2L);
        assertFalse(isOwner);
    }

    @Test
    void testIsOwnerOrAdminAdminTrue() {
        Authentication authentication = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean isOwnerOrAdmin = dataSecurityService.isOwnerOrAdmin(1L);
        assertTrue(isOwnerOrAdmin);
    }

    @Test
    void testIsOwnerOrAdminOwnerTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(new UserJpa());
        userDetails.setUserId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        CardJpa card = new CardJpa();
        UserJpa user = new UserJpa();
        user.setUserId(1L);
        card.setUser(user);
        when(cardRepository.findById(1L)).thenReturn(java.util.Optional.of(card));

        boolean isOwnerOrAdmin = dataSecurityService.isOwnerOrAdmin(1L);
        assertTrue(isOwnerOrAdmin);
    }

    @Test
    void testIsOwnerOrAdminFalse() {
        UserDetailsImpl userDetails = new UserDetailsImpl(new UserJpa());
        userDetails.setUserId(2L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        CardJpa card = new CardJpa();
        UserJpa user = new UserJpa();
        user.setUserId(1L);
        card.setUser(user);
        when(cardRepository.findById(1L)).thenReturn(java.util.Optional.of(card));

        boolean isOwnerOrAdmin = dataSecurityService.isOwnerOrAdmin(1L);
        assertFalse(isOwnerOrAdmin);
    }
}