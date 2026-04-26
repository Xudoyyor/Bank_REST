package com.backend.bankcards.controller.userController;

import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.cardsDTO.TransactionResponseDTO;
import com.backend.bankcards.dto.cardsDTO.TransferRequest;
import com.backend.bankcards.service.userService.UserCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
@Tag(name = "User Card Management", description = "Endpoints for users to manage their own cards")
public class UserCardController {

    private final UserCardService userCardService;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> getMyCard(@PathVariable Long id) {
        CardResponseDTO response = userCardService.getMyCardById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/block-requested")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> requestBlock(@PathVariable Long id) {
        userCardService.requestBlockCard(id);
        return ResponseEntity.ok("Your request to block your card has been received. It will be reviewed shortly.");
    }


    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequest request) {
        userCardService.transferBetweenMyCards(request);
        return ResponseEntity.ok("The transfer was successful.");
    }



    @GetMapping("/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions() {
        return ResponseEntity.ok(userCardService.getMyTransactionHistory());
    }





}