package com.backend.bankcards.dto.transactionDTO;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
