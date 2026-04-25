package com.backend.bankcards.service.adminSerivice;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardCreateRequestDTO;
import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardSearchFilter;
import com.backend.bankcards.entity.AuditLog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CardManageService {
    CardResponseDTO createCard(CardCreateRequestDTO requestDTO);

    void blockCard(Long cardId);
    void activateCard(Long cardId);
    List<AuditLogResponseDTO> getCardAuditHistory(Long cardId);
    List<CardResponseDTO> getTopBalanceCards(int limit);


}
