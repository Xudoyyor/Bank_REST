package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.usersDTO.ChangePasswordRequest;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import org.springframework.security.core.userdetails.User;

public interface UserService {
    UserResponseDTO getMyProfile();
    UserResponseDTO updateMyProfile(UserUpdateDTO request);
    void changePassword(ChangePasswordRequest request);
}
