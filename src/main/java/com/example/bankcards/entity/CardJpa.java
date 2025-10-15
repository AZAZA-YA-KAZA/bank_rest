package com.example.bankcards.entity;


import com.example.bankcards.util.CardStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "account")
@Entity
@Data
public class CardJpa {
    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserJpa user;

    @Column(name = "card_number", unique = true)
    private String cardNumber;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CardStatus status = CardStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CardJpa(UserJpa user, String cardNumber, Long balance, LocalDate expiryDate) {
        this.user = user;
        this.cardNumber = cardNumber;
        this.balance = balance;
        this.expiryDate = expiryDate;
        updateStatus();
    }

    public CardJpa() {

    }

    public void updateStatus() {
        if (expiryDate.isBefore(LocalDate.now())) {
            this.status = CardStatus.EXPIRED;
        }
    }
}