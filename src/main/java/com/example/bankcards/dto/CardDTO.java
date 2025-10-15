package com.example.bankcards.dto;


import com.example.bankcards.util.CardStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CardDTO {
    private Long cardId;
    private Long userId;
    private String maskedCardNumber; // **** **** **** 1234
    private String ownerName; // Владелец (из User)
    private Long balance;
    private LocalDate expiryDate;
    private CardStatus status;
    private LocalDateTime createdAt;

    public CardDTO(Long cardId, Long userId, String cardNumber, String ownerName, Long balance, LocalDate expiryDate, CardStatus status) {
        this.cardId = cardId;
        this.userId = userId;
        this.maskedCardNumber = maskCardNumber(cardNumber);
        this.ownerName = ownerName;
        this.balance = balance;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
