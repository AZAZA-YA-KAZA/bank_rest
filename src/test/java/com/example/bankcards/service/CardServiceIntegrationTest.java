package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardJpa;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.CardJpaRepository;
import com.example.bankcards.repository.TransactionJpaRepository;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.TransactionType;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.request.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private CardJpaRepository cardRepository;

    @Autowired
    private TransactionJpaRepository transactionRepository;

    private UserJpa user;
    private CardJpa card1;
    private CardJpa card2;

    @BeforeEach
    void setUp() {
        user = new UserJpa("testuser", "John", "Doe", "Smith", "test@example.com", "1234567890", "encodedPass", UserRole.USER);
        user.setUserId(1L);
        userRepository.save(user);

        card1 = new CardJpa(user, "1234567890123456", 1000L, LocalDate.now().plusYears(1));
        card2 = new CardJpa(user, "9876543210987654", 2000L, LocalDate.now().plusYears(1));
        cardRepository.save(card1);
        cardRepository.save(card2);
    }

    @Test
    void testCreateCard() {
        CardDTO cardDTO = cardService.createCard(user.getUserId(), "1111222233334444", 500L, LocalDate.now().plusYears(1));
        assertNotNull(cardDTO);
        assertEquals("**** **** **** 4444", cardDTO.getMaskedCardNumber());
        assertEquals(500L, cardDTO.getBalance());
        assertEquals(CardStatus.ACTIVE, cardDTO.getStatus());
    }

    @Test
    void testCreateCardDuplicateNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                cardService.createCard(user.getUserId(), "1234567890123456", 500L, LocalDate.now().plusYears(1)));
    }

    @Test
    void testGetUserCards() {
        Page<CardDTO> cards = cardService.getUserCards(user.getUserId(), PageRequest.of(0, 10), null);
        assertEquals(2, cards.getTotalElements());
        assertEquals("**** **** **** 3456", cards.getContent().get(0).getMaskedCardNumber());
        assertEquals("**** **** **** 7654", cards.getContent().get(1).getMaskedCardNumber());
    }

    @Test
    void testGetUserCardsWithStatusFilter() {
        card1.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card1);

        Page<CardDTO> cards = cardService.getUserCards(user.getUserId(), PageRequest.of(0, 10), CardStatus.BLOCKED);
        assertEquals(1, cards.getTotalElements());
        assertEquals("**** **** **** 3456", cards.getContent().get(0).getMaskedCardNumber());
    }

    @Test
    void testRequestBlockCard() {
        cardService.updateCardStatus(card1.getCardId(), CardStatus.BLOCKED, user.getUserId(), false);
        CardJpa updatedCard = cardRepository.findById(card1.getCardId()).orElseThrow();
        assertEquals(CardStatus.BLOCKED, updatedCard.getStatus());
    }

    @Test
    void testRequestBlockCardUnauthorized() {
        assertThrows(AccessDeniedException.class, () ->
                cardService.updateCardStatus(card1.getCardId(), CardStatus.BLOCKED, 999L, false));
    }

    @Test
    void testTransferBetweenCards() {
        TransferRequest request = new TransferRequest(card1.getCardId(), card2.getCardId(), 500L);
        cardService.transferBetweenCards(request, user.getUserId());

        CardJpa updatedFromCard = cardRepository.findById(card1.getCardId()).orElseThrow();
        CardJpa updatedToCard = cardRepository.findById(card2.getCardId()).orElseThrow();
        assertEquals(500L, updatedFromCard.getBalance());
        assertEquals(2500L, updatedToCard.getBalance());

        assertEquals(1, transactionRepository.findAll().size());
        assertEquals(TransactionType.TRANSFER, transactionRepository.findAll().get(0).getType());
    }

    @Test
    void testTransferBetweenCardsInsufficientFunds() {
        TransferRequest request = new TransferRequest(card1.getCardId(), card2.getCardId(), 5000L);
        assertThrows(IllegalArgumentException.class, () ->
                cardService.transferBetweenCards(request, user.getUserId()));
    }

    @Test
    void testTransferBetweenCardsInactiveCard() {
        card1.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card1);
        TransferRequest request = new TransferRequest(card1.getCardId(), card2.getCardId(), 500L);
        assertThrows(IllegalArgumentException.class, () ->
                cardService.transferBetweenCards(request, user.getUserId()));
    }

    @Test
    void testGetCardBalance() {
        CardDTO cardDTO = cardService.getCardById(card1.getCardId(), user.getUserId(), false);
        assertEquals(1000L, cardDTO.getBalance());
        assertEquals("**** **** **** 3456", cardDTO.getMaskedCardNumber());
    }

    @Test
    void testGetCardBalanceUnauthorized() {
        assertThrows(AccessDeniedException.class, () ->
                cardService.getCardById(card1.getCardId(), 999L, false));
    }

    @Test
    void testGetCardBalanceExpired() {
        card1.setExpiryDate(LocalDate.now().minusDays(1));
        cardRepository.save(card1);
        CardDTO cardDTO = cardService.getCardById(card1.getCardId(), user.getUserId(), false);
        assertEquals(CardStatus.EXPIRED, cardDTO.getStatus());
    }
}