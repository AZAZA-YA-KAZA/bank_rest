package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.request.TransferRequest;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Autowired
    private ObjectMapper objectMapper;

    private UserJpa user;
    private CardDTO cardDTO1;
    private CardDTO cardDTO2;

    @BeforeEach
    void setUp() {
        user = new UserJpa("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "encodedPass", UserRole.USER);
        user.setUserId(1L);

        cardDTO1 = new CardDTO(1L, 1L, "1234567890123456", "John Doe", 1000L, LocalDate.now().plusYears(1), CardStatus.ACTIVE);
        cardDTO2 = new CardDTO(2L, 1L, "9876543210987654", "John Doe", 2000L, LocalDate.now().plusYears(1), CardStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testGetMyCards() throws Exception {
        Page<CardDTO> cardPage = new PageImpl<>(List.of(cardDTO1, cardDTO2), PageRequest.of(0, 10), 2);
        when(cardService.getUserCards(eq(1L), any(), isNull())).thenReturn(cardPage);

        mockMvc.perform(get("/api/cards/my")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardId").value(1L))
                .andExpect(jsonPath("$.content[1].cardId").value(2L))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(cardService).getUserCards(eq(1L), any(), isNull());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testGetMyCardsWithStatusFilter() throws Exception {
        Page<CardDTO> cardPage = new PageImpl<>(List.of(cardDTO1), PageRequest.of(0, 10), 1);
        when(cardService.getUserCards(eq(1L), any(), eq(CardStatus.ACTIVE))).thenReturn(cardPage);

        mockMvc.perform(get("/api/cards/my")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardId").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(cardService).getUserCards(eq(1L), any(), eq(CardStatus.ACTIVE));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testRequestBlockCard() throws Exception {
        doNothing().when(cardService).updateCardStatus(eq(1L), eq(CardStatus.BLOCKED), eq(1L), eq(false));

        mockMvc.perform(post("/api/cards/1/block")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта заблокирована"));

        verify(cardService).updateCardStatus(eq(1L), eq(CardStatus.BLOCKED), eq(1L), eq(false));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testRequestBlockCardNotFound() throws Exception {
        doThrow(new RuntimeException("Карта не найдена")).when(cardService).updateCardStatus(eq(999L), eq(CardStatus.BLOCKED), eq(1L), eq(false));

        mockMvc.perform(post("/api/cards/999/block")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(cardService).updateCardStatus(eq(999L), eq(CardStatus.BLOCKED), eq(1L), eq(false));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testTransferBetweenCards() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, 500L);
        doNothing().when(cardService).transferBetweenCards(eq(request), eq(1L));

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод выполнен"));

        verify(cardService).transferBetweenCards(eq(request), eq(1L));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testTransferBetweenCardsInsufficientFunds() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, 5000L);
        doThrow(new IllegalArgumentException("Недостаточно средств")).when(cardService).transferBetweenCards(eq(request), eq(1L));

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cardService).transferBetweenCards(eq(request), eq(1L));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testGetCardBalance() throws Exception {
        when(cardService.getCardById(eq(1L), eq(1L), eq(false))).thenReturn(cardDTO1);

        mockMvc.perform(get("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(1L))
                .andExpect(jsonPath("$.balance").value(1000L))
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 3456"));

        verify(cardService).getCardById(eq(1L), eq(1L), eq(false));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testGetCardBalanceUnauthorized() throws Exception {
        when(cardService.getCardById(eq(999L), eq(1L), eq(false)))
                .thenThrow(new AccessDeniedException("Доступ запрещен"));

        mockMvc.perform(get("/api/cards/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(cardService).getCardById(eq(999L), eq(1L), eq(false));
    }
}