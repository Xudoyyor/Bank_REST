package com.backend.bankcards.dto;

import com.backend.bankcards.enums.Role;

public record UserSearchFilter(
        String query,
        String email,
        String phone,
        Role role,
        Boolean isActive,
        Integer page,
        Integer size
) {

    public int getPage() { return page != null ? page : 0; }
    public int getSize() { return size != null ? size : 10; }
}