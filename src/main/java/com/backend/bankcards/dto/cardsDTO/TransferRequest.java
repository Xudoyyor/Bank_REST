package com.backend.bankcards.dto.cardsDTO;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
