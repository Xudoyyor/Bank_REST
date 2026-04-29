package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.transactionDTO.TransactionResponseDTO;
import com.backend.bankcards.dto.transactionDTO.TransferRequest;
import com.backend.bankcards.entity.Card;
import com.backend.bankcards.entity.TransactionEntity;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.exception.InsufficientFundsException;
import com.backend.bankcards.exception.ResourceNotFoundException;
import com.backend.bankcards.repository.CardRepository;
import com.backend.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCardServiceImpl implements UserCardService {
    private final TransactionRepository transactionRepo;
    private final CardRepository cardRepo;

    @Override
    public CardResponseDTO getMyCardById(Long cardId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));


        if (!card.getUser().getUsername().equals(currentUsername)) {
            log.warn("User {} tried to access card ID {} which belongs to another user!", currentUsername, cardId);
            throw new AccessDeniedException("You don't have permission to view this card");
        }

        return mapToResponse(card);
    }

    private CardResponseDTO mapToResponse(Card card) {
        return new CardResponseDTO(
                card.getId(),
                card.getMaskedNumber(),
                card.getExpirationDate(),
                card.getBalance(),
                card.getStatus(),
                card.getCardType(),
                card.getCardCategory(),
                card.getOwnerName(),
                card.getCreatedAt()
        );
    }


    @Override
    public void requestBlockCard(Long cardId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getUsername().equals(currentUsername)) {
            log.warn("User {} tried to block card ID {} which belongs to someone else!", currentUsername, cardId);
            throw new AccessDeniedException("You can only request to block a card that belongs to you!");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Card is already blocked");
        }
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        card.setUpdatedAt(LocalDateTime.now());
        cardRepo.save(card);
        log.info("User {} requested to block card ID {}", currentUsername, cardId);
    }


    @Override
    @Transactional
    public void transfer(TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Card fromCard = cardRepo.findById(request.fromCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Exit card not found"));

        Card toCard = cardRepo.findById(request.toCardId())
                .orElseThrow(() -> new ResourceNotFoundException("No credit card found."));

        if (!fromCard.getUser().getUsername().equals(username) ||
                !toCard.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only transfer between your own cards!");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Cards must be active!");
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Not enough money!");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepo.save(fromCard);
        cardRepo.save(toCard);


        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setFromCard(fromCard);
        transactionEntity.setToCard(toCard);
        transactionEntity.setAmount(request.amount());
        transactionEntity.setStatus("SUCCESS");

        transactionRepo.save(transactionEntity);

        log.info("Transfer successful and recorded. ID: {}", transactionEntity.getId());
    }



    @Override
    public List<TransactionResponseDTO> getMyTransactionHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        List<Card> myCards = cardRepo.findAllByUserUsername(username);

        if (myCards.isEmpty()) {
            return Collections.emptyList();
        }


        List<TransactionEntity> transactions = transactionRepo.findAllByFromCardInOrToCardInOrderByCreatedAtDesc(myCards, myCards);

        return transactions.stream()
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getFromCard().getMaskedNumber(),
                        t.getToCard().getMaskedNumber(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getCreatedAt()
                ))
                .toList();
    }


    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TransactionEntity transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));

        boolean isOwner = (transaction.getFromCard() != null && transaction.getFromCard().getUser().getUsername().equals(username)) ||
                (transaction.getToCard() != null && transaction.getToCard().getUser().getUsername().equals(username));

        if (!isOwner) {
            throw new AccessDeniedException("Access denied to this transaction history");
        }

        return mapToTransactionResponse(transaction);
    }

    private TransactionResponseDTO mapToTransactionResponse(TransactionEntity t) {
        return new TransactionResponseDTO(
                t.getId(),
                t.getFromCard() != null ? t.getFromCard().getMaskedNumber() : "N/A",
                t.getToCard() != null ? t.getToCard().getMaskedNumber() : "N/A",
                t.getAmount(),
                t.getStatus(),
                t.getCreatedAt()
        );
    }



    @Override
    public List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getUsername().equals(username)) {
            log.warn("User {} tried to access transaction history of card ID {} which belongs to another user!", username, cardId);
            throw new AccessDeniedException("You don't have permission to view this card's transactions");
        }

        List<TransactionEntity> transactions = transactionRepo.findAllByFromCardOrToCardOrderByCreatedAtDesc(card, card);

        return transactions.stream()
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getFromCard() != null ? t.getFromCard().getMaskedNumber() : "N/A",
                        t.getToCard() != null ? t.getToCard().getMaskedNumber() : "N/A",
                        t.getAmount(),
                        t.getStatus(),
                        t.getCreatedAt()
                ))
                .toList();
    }



    @Override
    public BigDecimal getCardBalance(Long cardId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found, ID: " + cardId));

        if (!card.getUser().getUsername().equals(currentUsername)) {
            log.warn("User {} tried to view balance of card ID {} which belongs to another user!", currentUsername, cardId);
            throw new AccessDeniedException("You do not have permission to view this card balance!");
        }
        return card.getBalance();
    }





    



}