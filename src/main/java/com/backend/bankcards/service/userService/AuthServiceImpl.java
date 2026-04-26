package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.authDTO.AuthResponse;
import com.backend.bankcards.dto.authDTO.LoginRequest;
import com.backend.bankcards.dto.authDTO.RegisterRequest;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.Role;
import com.backend.bankcards.repository.UserRepository;
import com.backend.bankcards.service.securityService.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username allaqachon band!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setRole(Role.ROLE_USER);
        user.setActive(true);
        user.setBlocked(false);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi!"));


        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(token,user.getUsername(),user.getRole().name());
    }
}