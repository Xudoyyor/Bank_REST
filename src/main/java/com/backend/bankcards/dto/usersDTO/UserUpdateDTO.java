package com.backend.bankcards.dto.usersDTO;

public record UserUpdateDTO(
        String firstName,
        String lastName,
        String email,
        String phone,
        String role
) {}