package com.example.bankcards.util.request;

public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        Long amount
) {
}
