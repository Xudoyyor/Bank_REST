package com.backend.bankcards.dto.cardsDTO;

import com.backend.bankcards.enums.CardCategory;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.enums.CardType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CardResponseDTO(
        Long id,
        String cardNumber,
        java.time.LocalDate expiryDate,
        BigDecimal balance,
        CardStatus status,
        CardType cardType,
        CardCategory cardCategory,
        String ownerFullName,
        LocalDateTime createdAt
) {
}
