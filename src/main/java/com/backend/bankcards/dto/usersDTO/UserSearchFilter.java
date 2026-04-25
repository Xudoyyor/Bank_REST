package com.backend.bankcards.dto.usersDTO;

import com.backend.bankcards.enums.Role;

public record UserSearchFilter(
        String query,
        String email,
        String phone,
        Role role,
        Boolean isActive,
        int page,
        int size
) {
}