package com.backend.bankcards.dto.cardsDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CardCreateRequestDTO(
        @NotNull(message = "User ID cannot be null")
        Long userId,

        @NotBlank(message = "Card type is required")
        String cardType,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Required")
        String bankName

) {
}
