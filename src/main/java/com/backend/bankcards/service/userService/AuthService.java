package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.authDTO.AuthResponse;
import com.backend.bankcards.dto.authDTO.LoginRequest;
import com.backend.bankcards.dto.authDTO.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
