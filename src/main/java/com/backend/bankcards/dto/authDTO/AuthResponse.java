package com.backend.bankcards.dto.authDTO;

public record AuthResponse(
        String token,
        String username,
        String role
) {
}
