package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.request.CardRequest;
import com.example.bankcards.util.request.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Admin: Создание карты
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardRequest request, @RequestParam Long userId, @RequestParam LocalDate expiryDate) {
        return ResponseEntity.ok(cardService.createCard(userId, request.accountNumber(), request.balance(), expiryDate));
    }

    // User/Admin: Просмотр карты по ID
    @GetMapping("/{cardId}")
    @PreAuthorize("@dataSecurityService.isOwnerOrAdmin(#cardId)") // Кастомный сервис для проверки владельца или админа
    public ResponseEntity<CardDTO> getCard(@PathVariable Long cardId) {
        // Предполагаем currentUserId из SecurityContext
        Long currentUserId = getCurrentUserId(); // Метод для получения ID из JWT
        boolean isAdmin = isAdmin(); // Проверка роли
        return ResponseEntity.ok(cardService.getCardById(cardId, currentUserId, isAdmin));
    }

    // User: Просмотр своих карт с пагинацией и фильтром
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardDTO>> getMyCards(Pageable pageable, @RequestParam(required = false) CardStatus status) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cardService.getUserCards(userId, pageable, status));
    }

    // Admin: Просмотр всех карт
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDTO>> getAllCards(Pageable pageable, @RequestParam(required = false) CardStatus status) {
        return ResponseEntity.ok(cardService.getAllCards(pageable, status));
    }

    // User: Запрос блокировки
    @PostMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> requestBlock(@PathVariable Long cardId) {
        Long userId = getCurrentUserId();
        cardService.updateCardStatus(cardId, CardStatus.BLOCKED, userId, false);
        return ResponseEntity.ok("Карта заблокирована");
    }

    // Admin: Активация/Блокировка
    @PostMapping("/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable Long cardId, @RequestParam CardStatus status) {
        cardService.updateCardStatus(cardId, status, null, true);
        return ResponseEntity.ok("Статус обновлен");
    }

    // User: Перевод между своими картами
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        Long userId = getCurrentUserId();
        cardService.transferBetweenCards(request, userId);
        return ResponseEntity.ok("Перевод выполнен");
    }

    // Admin/User: Удаление карты
    @DeleteMapping("/{cardId}")
    @PreAuthorize("@dataSecurityService.isOwnerOrAdmin(#cardId)")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        Long userId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        cardService.deleteCard(cardId, userId, isAdmin);
        return ResponseEntity.ok("Карта удалена");
    }

    // Заглушки для currentUserId и isAdmin (реализовать через SecurityContextHolder)
    private Long getCurrentUserId() {
        // Логика извлечения из JWT
        return 1L; // Заглушка
    }

    private boolean isAdmin() {
        // Проверка роли
        return false; // Заглушка
    }
}
