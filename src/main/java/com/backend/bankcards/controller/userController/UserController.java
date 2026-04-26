package com.backend.bankcards.controller.userController;

import com.backend.bankcards.dto.usersDTO.ChangePasswordRequest;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import com.backend.bankcards.service.adminSerivice.UserManageService;
import com.backend.bankcards.service.userService.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for managing user profile")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody @Valid UserUpdateDTO request) {
        UserResponseDTO response = userService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully.");
    }
}