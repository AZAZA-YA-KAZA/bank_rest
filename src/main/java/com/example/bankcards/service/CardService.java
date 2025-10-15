package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardJpa;
import com.example.bankcards.entity.TransactionJpa;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.CardJpaRepository;
import com.example.bankcards.repository.TransactionJpaRepository;
import com.example.bankcards.repository.UserJpaRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.TransactionType;
import com.example.bankcards.util.request.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
@Service
public class CardService {
    private final CardJpaRepository cardRepository;
    private final UserJpaRepository userRepository;
    private final TransactionJpaRepository transactionRepository;
    public CardService(CardJpaRepository cardRepository, UserJpaRepository userRepository, TransactionJpaRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    @Transactional
    public CardDTO createCard(Long userId, String cardNumber, Long balance, LocalDate expiryDate) {
        if (cardRepository.existsByCardNumber(cardNumber)) {
            throw new IllegalArgumentException("Номер карты уже занят");
        }
        UserJpa user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        CardJpa card = new CardJpa(user, encryptCardNumber(cardNumber), balance, expiryDate);
        cardRepository.save(card);
        return mapToDTO(card);
    }
    public CardDTO getCardById(Long cardId, Long currentUserId, boolean isAdmin) {
        CardJpa card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        if (!isAdmin && !card.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        card.updateStatus(); // Проверка истечения
        return mapToDTO(card);
    }
    @Transactional
    public void updateCardStatus(Long cardId, CardStatus status, Long currentUserId, boolean isAdmin) {
        CardJpa card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        if (!isAdmin && !card.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        if (!isAdmin && status != CardStatus.BLOCKED) { // User может только запрашивать блокировку
            throw new AccessDeniedException("Пользователь может только блокировать карту");
        }
        card.setStatus(status);
        cardRepository.save(card);
    }
    @Transactional
    public void deleteCard(Long cardId, Long currentUserId, boolean isAdmin) {
        CardJpa card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        if (!isAdmin && !card.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        cardRepository.delete(card);
    }
    public Page<CardDTO> getUserCards(Long userId, Pageable pageable, CardStatus statusFilter) {
        Specification<CardJpa> spec = (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId);
        if (statusFilter != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusFilter));
        }
        return cardRepository.findAll(spec, pageable).map(this::mapToDTO);
    }
    public Page<CardDTO> getAllCards(Pageable pageable, CardStatus statusFilter) {
        Specification<CardJpa> spec = null;
        if (statusFilter != null) {
            spec = (root, query, cb) -> cb.equal(root.get("status"), statusFilter);
        }
        return cardRepository.findAll(spec, pageable).map(this::mapToDTO);
    }
    @Transactional
    public void transferBetweenCards(TransferRequest request, Long currentUserId) {
        CardJpa fromCard = cardRepository.findById(request.fromCardId()).orElseThrow(() -> new RuntimeException("Карта-отправитель не найдена"));
        CardJpa toCard = cardRepository.findById(request.toCardId()).orElseThrow(() -> new RuntimeException("Карта-получатель не найдена"));
        if (!fromCard.getUser().getUserId().equals(currentUserId) || !toCard.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Перевод возможен только между своими картами");
        }
        if (fromCard.getBalance() < request.amount()) {
            throw new IllegalArgumentException("Недостаточно средств");
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Карты должны быть активны");
        }
        fromCard.setBalance(fromCard.getBalance() - request.amount());
        toCard.setBalance(toCard.getBalance() + request.amount());
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        TransactionJpa transaction = new TransactionJpa(fromCard, toCard, request.amount(), TransactionType.TRANSFER);
        transactionRepository.save(transaction);
    }
    private CardDTO mapToDTO(CardJpa card) {
        String ownerName = card.getUser().getFirstName() + " " + card.getUser().getSurName();
        return new CardDTO(card.getCardId(), card.getUser().getUserId(), card.getCardNumber(), ownerName, card.getBalance(), card.getExpiryDate(), card.getStatus());
    }
    private String encryptCardNumber(String cardNumber) {
// Простое шифрование (в реальности использовать AES или подобное)
        return cardNumber; // Заглушка
    }
}