package com.backend.bankcards.controller.adminController;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserSearchFilter;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import com.backend.bankcards.service.adminSerivice.UserManageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Management for Admins", description = "Endpoints for managing users")
public class UserManageController {

    private static final Logger log = LoggerFactory.getLogger(UserManageController.class);

    private final UserManageService userManageService;


    @PatchMapping("/{userId}/block")
    public ResponseEntity<Map<String, String>> blockUser(@PathVariable Long userId) {
        userManageService.blockUser(userId);
        return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
    }


    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        userManageService.activateUser(userId);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userManageService.getUserById(userId));
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody @Valid UserUpdateDTO updateRequest) { // @Valid - xavfsizlik uchun
        return ResponseEntity.ok(userManageService.updateUser(userId, updateRequest));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userManageService.softDeleteUser(userId);
        return ResponseEntity.ok(Map.of(
                "message", "User with ID " + userId + " has been soft-deleted successfully",
                "status", "DEACTIVATED"
        ));
    }



    @GetMapping("/{userId}/audit")
    public ResponseEntity<List<AuditLogResponseDTO>> getUserAuditHistory(@PathVariable Long userId) {
        List<AuditLogResponseDTO> history = userManageService.getUserAuditHistory(userId);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(@ModelAttribute UserSearchFilter filter) {
        log.info("Admin searching users with filter: {}", filter);
        Page<UserResponseDTO> result = userManageService.searchUsers(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardResponseDTO>> getUserCards(@PathVariable Long id) {
        log.info("REST request to get all cards for user ID: {}", id);
        List<CardResponseDTO> cards = userManageService.getUserCards(id);
        return ResponseEntity.ok(cards);
    }
}










