package com.backend.bankcards.dto.usersDTO;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        com.backend.bankcards.enums.Role role,
        Boolean isActive,
        LocalDateTime createdAt

) {
}