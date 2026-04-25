package com.backend.bankcards.service.adminSerivice;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardCreateRequestDTO;
import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardSearchFilter;
import com.backend.bankcards.entity.AuditLog;
import com.backend.bankcards.entity.Card;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.CardCategory;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.enums.CardType;
import com.backend.bankcards.exception.ResourceNotFoundException;
import com.backend.bankcards.repository.AuditLogRepository;
import com.backend.bankcards.repository.CardRepository;
import com.backend.bankcards.repository.UserRepository;
import com.backend.bankcards.service.securityService.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardManageServiceImpl implements CardManageService {
    private static final Logger log = LoggerFactory.getLogger(CardManageServiceImpl.class);

    private final CardRepository cardRepo;
    private final UserRepository userRepo;
    private final AuditLogRepository auditLogRepo;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CardResponseDTO createCard(CardCreateRequestDTO request) {
        try {
            String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            UserEntity user = userRepo.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));

            if (user.getBlocked() != null && user.getBlocked()) {
                throw new RuntimeException("It is not possible to open a card for a blocked user!");
            }

            if (user.getActive() != null && !user.getActive()) {
                throw new RuntimeException("User profile is inactive!");
            }


            CardCategory category = CardCategory.valueOf(request.category().toUpperCase().trim());
            CardType type = CardType.valueOf(request.cardType().toUpperCase().trim());

            String rawNumber = generateRawNumber(category);
            String masked = rawNumber.substring(0,4) + " **** **** " + rawNumber.substring(12);
            String rawCvv = String.format("%03d", (int) (Math.random() * 1000));

            Card card = new Card();
            card.setUser(user);
            card.setOwnerName(user.getFirstName() + " " + user.getLastName());
            card.setBankName(request.bankName());
            card.setCardType(type);
            card.setCardCategory(category);
            card.setMaskedNumber(masked);
            card.setCardNumber(encryptionUtil.encrypt(rawNumber));
            card.setCvvHash(passwordEncoder.encode(rawCvv));

            card.setExpirationDate(LocalDate.now().plusYears(4));
            card.setBalance(BigDecimal.ZERO);
            card.setStatus(CardStatus.ACTIVE);


            cardRepo.save(card);

            saveAuditLog(user, "CARD_CREATED", "New card created for " + user.getUsername(), adminUsername, card.getId());

            return mapToResponse(card);

        } catch (IllegalArgumentException e) {
            System.err.println("Enum value error: " + e.getMessage());
            throw new RuntimeException("Incorrect card type or category entered");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String generateRawNumber(CardCategory category) {
        String prefix = switch (category) {
            case UZCARD -> "8600";
            case HUMO -> "9860";
            case VISA -> "4000";
            case MASTERCARD -> "5100";
            default -> "0000";
        };
        return prefix + String.format("%012d", (long) (Math.random() * 1_000_000_000_000L));
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



    private void saveAuditLog(UserEntity user, String action, String desc, String admin,Long entityId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType("CARD");
        log.setUserId(user);
        log.setDescription(desc);
        log.setPerformedBy(admin);
        log.setEntityId(entityId);
        auditLogRepo.save(log);
    }




    @Override
    @Transactional
    public void blockCard(Long cardId) {
        updateCardStatus(cardId, CardStatus.BLOCKED, "CARD_BLOCKED");
    }

    @Override
    @Transactional
    public void activateCard(Long cardId) {
        updateCardStatus(cardId, CardStatus.ACTIVE, "CARD_ACTIVATED");
    }



    private void updateCardStatus(Long cardId, CardStatus newStatus, String action) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        card.setStatus(newStatus);
        card.setUpdatedAt(LocalDateTime.now());
        card.setStatusChangedBy(adminUsername);



        cardRepo.save(card);

        saveAuditLog(
                card.getUser(),
                action,
                "Admin " + adminUsername + " changed status to " + newStatus + " for card ID: " + card.getId(),
                adminUsername,
                card.getId()
        );

        log.info("Card ID {} status changed to {} by {}", cardId, newStatus, adminUsername);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getCardAuditHistory(Long cardId) {
        if (!cardRepo.existsById(cardId)) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }


        List<AuditLog> logs = auditLogRepo.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("CARD", cardId);


        return logs.stream()
                .map(log -> new AuditLogResponseDTO(
                        log.getId(),
                        log.getAction(),
                        log.getDescription(),
                        log.getPerformedBy(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDTO> getTopBalanceCards(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Card> cards = cardRepo.findTopBalanceCards(pageable);

        return cards.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }




}