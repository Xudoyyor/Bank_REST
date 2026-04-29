package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.usersDTO.ChangePasswordRequest;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.Role;
import com.backend.bankcards.exception.ResourceNotFoundException;
import com.backend.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }


    @Override
    public UserResponseDTO updateMyProfile(UserUpdateDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        UserEntity updatedUser = userRepo.save(user);

        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole(),
                updatedUser.getIsActive(),
                updatedUser.getCreatedAt()
        );
    }


    @Override
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("The new passwords did not match!");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The old password was entered incorrectly!");
        }


        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);

        log.info("User {} successfully changed their password", username);
    }



}
