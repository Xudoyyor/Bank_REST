package com.backend.bankcards.dto;

import java.time.LocalDateTime;

public record AuditLogResponseDTO(
        Long id,
        String action,
        String description,
        String performedBy,
        LocalDateTime createdAt
) {}