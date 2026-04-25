package com.backend.bankcards.dto.cardsDTO;

import com.backend.bankcards.enums.CardCategory;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.enums.CardType;

public record CardSearchFilter(
        String cardHolderName,
        String bankName,
        CardStatus status,
        CardType cardType,
        CardCategory cardCategory,
        int page,
        int size
) {}