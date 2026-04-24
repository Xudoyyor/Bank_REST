package com.backend.bankcards.dto;

public record UserResponseDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        Boolean isActive
) {

}