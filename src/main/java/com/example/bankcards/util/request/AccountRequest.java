package com.example.bankcards.util.request;

public record AccountRequest(
        Long balance,
        String accountNumber
) {
}
