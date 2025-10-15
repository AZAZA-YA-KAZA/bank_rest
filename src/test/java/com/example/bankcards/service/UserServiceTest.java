package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserJpa userJpa;

    @BeforeEach
    void setUp() {
        userJpa = new UserJpa("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "encodedPass", UserRole.USER);
        userJpa.setUserId(1L);
    }

    @Test
    void testCreateUser() {
        when(userJpaRepository.existsByUsername(anyString())).thenReturn(false);
        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);
        when(userJpaRepository.existsByTelephone(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(userJpaRepository.save(any(UserJpa.class))).thenReturn(userJpa);

        UserDTO userDTO = userService.createUser("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "password", "USER");

        assertEquals("testuser", userDTO.getUsername());

        verify(userJpaRepository).save(any(UserJpa.class));
    }

    @Test
    void testCreateUserDuplicate() {
        when(userJpaRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "password", "USER"));

        verify(userJpaRepository, never()).save(any(UserJpa.class));
    }

    @Test
    void testUpdateUser() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userJpa));
        when(userJpaRepository.existsByUsername(anyString())).thenReturn(false);
        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);
        when(userJpaRepository.existsByTelephone(anyString())).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("newencodedPass");
        doNothing().when(userJpaRepository).updateById(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(UserRole.class));

        UserDTO userDTO = userService.updateUser(1L, "newuser", "NewJohn", "NewDoe", "NewSmith", "newemail@example.com", "9876543210", "newpassword", "ADMIN");

        assertEquals("newuser", userDTO.getUsername());

        verify(userJpaRepository).updateById(eq(1L), eq("newuser"), eq("NewJohn"), eq("NewDoe"), eq("NewSmith"), eq("newemail@example.com"), eq("9876543210"), eq("newencodedPass"), eq(UserRole.ADMIN));
    }

    @Test
    void testGetById() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userJpa));

        UserJpa foundUser = userService.getById(1L);

        assertEquals(1L, foundUser.getUserId());

        verify(userJpaRepository).findById(1L);
    }

    @Test
    void testGetAllUsers() {
        Page<UserJpa> userPage = new PageImpl<>(List.of(userJpa), PageRequest.of(0, 10), 1);
        when(userJpaRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        Page<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());

        verify(userJpaRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userJpaRepository).deleteById(1L);

        String result = userService.deleteUser(1L);

        assertEquals("Ok", result);

        verify(userJpaRepository).deleteById(1L);
    }
}