package com.backend.bankcards.controller.adminController;

import com.backend.bankcards.dto.UserSearchFilter;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.service.adminSerivice.UserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/admin/users")
//@PreAuthorize("hasRole('ADMIN')")
public class UserManageController {
    private static final Logger log = LoggerFactory.getLogger(UserManageController.class);

    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @GetMapping
    public ResponseEntity<Page<?>> getAllUsers(UserSearchFilter filter) {
        log.info("Called method getAllUsers");
        return ResponseEntity.ok(userManageService.searchUsers(filter));
    }



    @PatchMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        log.info("Request to block user with ID: {}", userId);
        try {
            userManageService.blockUser(userId);

            return ResponseEntity.ok(Map.of(
                    "message", "User successfully blocked",
                    "userId", userId,
                    "status", "BLOCKED"
            ));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.error("Block error: User with ID {} not found", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found!"));
            }

            log.error("Internal error blocking user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected system error has occurred."));
        }
    }


    @PatchMapping("/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        log.info("Admin request to activate user with ID: {}", userId);
        try {
            userManageService.activateUser(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User successfully activated",
                    "userId", userId,
                    "newStatus", "ACTIVE"
            ));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.error("Activation failed: User {} not found", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            log.error("Error during user activation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected system error has occurred."));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        log.info("Admin request to fetch user details with ID: {}", userId);
        try {
            UserEntity user = userManageService.getUserById(userId);

            user.setPassword(null);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found", "details", e.getMessage()));
        }
    }


    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserEntity updateRequest) {
        log.info("Admin request to update user ID: {}", userId);
        try {
            UserEntity updatedUser = userManageService.updateUser(userId, updateRequest);

            updatedUser.setPassword(null);

            return ResponseEntity.ok(Map.of(
                    "message", "User dates successfully updated",
                    "data", updatedUser
            ));

        } catch (RuntimeException e) {
            log.error("Update error for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("Admin request to soft delete user ID: {}", userId);
        try {
            userManageService.softDeleteUser(userId);

            return ResponseEntity.ok(Map.of(
                    "message", "User deleted on database(soft)",
                    "userId", userId
            ));

        } catch (RuntimeException e) {
            log.error("Delete error for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }







}
