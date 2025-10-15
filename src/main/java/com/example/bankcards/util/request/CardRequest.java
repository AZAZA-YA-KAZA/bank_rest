package com.example.bankcards.util.request;

public record CardRequest(
        Long balance,
        String accountNumber
) {
}
