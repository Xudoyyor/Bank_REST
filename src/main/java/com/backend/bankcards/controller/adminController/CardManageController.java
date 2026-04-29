package com.backend.bankcards.controller.adminController;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardCreateRequestDTO;
import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardSearchFilter;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.service.adminSerivice.CardManageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.AlgorithmConstraints;
import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management for Admins", description = "Endpoints for managing cards")
public class CardManageController {
    private static final Logger log = LoggerFactory.getLogger(CardManageController.class);

    private final CardManageService cardManageService;

    @PostMapping("/create")
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody @Valid CardCreateRequestDTO requestDTO) {
        CardResponseDTO response = cardManageService.createCard(requestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PatchMapping("/{id}/block")
    public ResponseEntity<String> blockCard(@PathVariable Long id) {
        log.info("REST request to block card ID: {}", id);
        cardManageService.blockCard(id);
        return ResponseEntity.ok("Card with ID " + id + " has been successfully blocked.");
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateCard(@PathVariable Long id) {
        log.info("REST request to activate card ID: {}", id);
        cardManageService.activateCard(id);
        return ResponseEntity.ok("Card with ID " + id + " has been successfully activated.");
    }


    @GetMapping("/{id}/history")
    public ResponseEntity<List<AuditLogResponseDTO>> getCardHistory(@PathVariable Long id) {
        log.info("REST request to get history for card ID: {}", id);
        List<AuditLogResponseDTO> history = cardManageService.getCardAuditHistory(id);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/top-balance")
    public ResponseEntity<List<CardResponseDTO>> getTopCards(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("REST request to get top {} cards by balance", limit);
        List<CardResponseDTO> topCards = cardManageService.getTopBalanceCards(limit);
        return ResponseEntity.ok(topCards);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CardResponseDTO>> searchCards(@ModelAttribute CardSearchFilter filter) {
        log.info("REST request to search cards with filter: {}", filter);
        Page<CardResponseDTO> result = cardManageService.searchCards(filter);
        return ResponseEntity.ok(result);
    }

}