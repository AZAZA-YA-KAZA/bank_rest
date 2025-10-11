package com.example.bankcards.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long accountId;
    private Long userId;
    private Long balance;
    private String accountNumber;
    private LocalDateTime createAt = LocalDateTime.now();

    public AccountDTO(Long userId, Long balance, String accountNumber, Long fromTransations, Long toTransactions) {
        this.userId = userId;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }
}
