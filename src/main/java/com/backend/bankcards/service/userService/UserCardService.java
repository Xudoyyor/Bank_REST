package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.cardsDTO.TransactionResponseDTO;
import com.backend.bankcards.dto.cardsDTO.TransferRequest;

import java.math.BigDecimal;
import java.util.List;

public interface UserCardService {
    CardResponseDTO getMyCardById(Long cardId);
    void requestBlockCard(Long cardId);
    void transferBetweenMyCards(TransferRequest request);
    List<TransactionResponseDTO> getMyTransactionHistory();
    TransactionResponseDTO getTransactionById(Long id);
    List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId);
    BigDecimal getCardBalance(Long cardId);


}
