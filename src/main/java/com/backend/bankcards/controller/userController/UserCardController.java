package com.backend.bankcards.controller.userController;

import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.TransactionResponseDTO;
import com.backend.bankcards.dto.TransferRequest;
import com.backend.bankcards.service.userService.UserCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions() {
        return ResponseEntity.ok(userCardService.getMyTransactionHistory());
    }


    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionDetails(@PathVariable Long id) {
        return ResponseEntity.ok(userCardService.getTransactionById(id));
    }


    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getCardTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(userCardService.getTransactionHistoryByCardId(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        BigDecimal balance = userCardService.getCardBalance(id);
        return ResponseEntity.ok(balance);
    }





}