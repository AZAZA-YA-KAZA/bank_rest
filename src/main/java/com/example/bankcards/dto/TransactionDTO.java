package com.example.bankcards.dto;

import com.example.bankcards.util.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private TransactionType type;
    private LocalDateTime createAt = LocalDateTime.now();

    public TransactionDTO(Long fromAccountId, Long toAccountId, Long amount, TransactionType type) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
    }
}
