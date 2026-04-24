package com.backend.bankcards.dto;

public record AuthResponseDTO(
        String token,
        String username,
        String role
) {

}