package com.backend.bankcards.dto.usersDTO;

import com.backend.bankcards.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(
        String firstName,

        String lastName,

        @Email(message = "There is an error in the email format.")
        String email,

        String phone,

        Role role
) {}