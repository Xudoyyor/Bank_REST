package com.backend.bankcards.dto.transactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        String fromCardMasked,
        String toCardMasked,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}
