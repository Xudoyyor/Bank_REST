package com.backend.bankcards.dto;

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
