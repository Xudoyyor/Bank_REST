package com.backend.bankcards.dto;

public record UserUpdateDTO(
        String firstName,
        String lastName,
        String email,
        String phone,
        String role
) {}