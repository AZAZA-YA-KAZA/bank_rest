package com.example.bankcards.controller;


import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.request.UserRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserDTO userDTO;
    private UserJpa userJpa;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "password", "USER");
        userDTO = new UserDTO("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890");
        userJpa = new UserJpa("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "encodedPass", UserRole.USER);
        userJpa.setUserId(1L);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testCreateUser() throws Exception {
        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(userDTO);

        mockMvc.perform(post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).createUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testGetUser() throws Exception {
        when(userService.getById(eq(1L))).thenReturn(userJpa);

        mockMvc.perform(get("/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(userService).getById(eq(1L));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testGetAllUsers() throws Exception {
        Page<UserDTO> userPage = new PageImpl<>(List.of(userDTO), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any())).thenReturn(userPage);

        mockMvc.perform(get("/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService).getAllUsers(any());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(userDTO);

        mockMvc.perform(post("/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).updateUser(eq(1L), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(eq(1L))).thenReturn("Ok");

        mockMvc.perform(delete("/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));

        verify(userService).deleteUser(eq(1L));
    }
}