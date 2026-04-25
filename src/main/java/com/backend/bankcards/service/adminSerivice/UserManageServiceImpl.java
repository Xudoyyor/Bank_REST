package com.backend.bankcards.service.adminSerivice;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserSearchFilter;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import com.backend.bankcards.entity.AuditLog;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.Role;
import com.backend.bankcards.exception.ResourceNotFoundException;
import com.backend.bankcards.repository.AuditLogRepository;
import com.backend.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService{
    private static final Logger log = LoggerFactory.getLogger(UserManageServiceImpl.class);

    private final UserRepository userRepo;
    private final AuditLogRepository auditLogRepo;



    @Override
    @Transactional
    public void blockUser(Long userId) {
        String adminUsername = getCurrentAdminUsername();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        user.setBlocked(true);
        user.setActive(false);
        userRepo.save(user);

        saveAuditLog(user, "USER_BLOCKED", "Admin " + adminUsername + " blocked user " + user.getUsername(), adminUsername);
    }




    @Override
    @Transactional
    public void activateUser(Long userId) {

        String adminUsername = getCurrentAdminUsername();
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setBlocked(false);
        user.setActive(true);
        userRepo.save(user);

        saveAuditLog(
                user,
                "USER_ACTIVATED",
                "User '" + user.getUsername() + "' was activated and unblocked by admin: " + adminUsername,
                adminUsername
        );
    }



    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        UserEntity targetUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        return new UserResponseDTO(
                targetUser.getId(),
                targetUser.getFirstName(),
                targetUser.getLastName(),
                targetUser.getUsername(),
                targetUser.getEmail(),
                targetUser.getPhone(),
                targetUser.getRole(),
                targetUser.getActive(),
                targetUser.getCreatedAt()
        );
    }



    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateRequest) {
        String adminUsername = getCurrentAdminUsername();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        StringBuilder changes = new StringBuilder("Updated: ");

        if (updateRequest.firstName() != null && !updateRequest.firstName().equals(user.getFirstName())) {
            changes.append(String.format("FirstName[%s -> %s] ", user.getFirstName(), updateRequest.firstName()));
            user.setFirstName(updateRequest.firstName());
        }

        if (updateRequest.lastName() != null && !updateRequest.lastName().equals(user.getLastName())) {
            changes.append(String.format("LastName[%s -> %s] ", user.getLastName(), updateRequest.lastName()));
            user.setLastName(updateRequest.lastName());
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(user.getEmail())) {
            changes.append(String.format("Email[%s -> %s] ", user.getEmail(), updateRequest.email()));
            user.setEmail(updateRequest.email());
        }

        if (updateRequest.phone() != null && !updateRequest.phone().equals(user.getPhone())) {
            changes.append(String.format("Phone[%s -> %s] ", user.getPhone(), updateRequest.phone()));
            user.setPhone(updateRequest.phone());
        }

        if (updateRequest.role() != null && !updateRequest.role().equals(user.getRole().name())) {
            changes.append(String.format("Role[%s -> %s] ", user.getRole(), updateRequest.role()));
            user.setRole(Role.valueOf(updateRequest.role()));
        }

        String finalDescription = changes.length() > 9 ? changes.toString() : "Update called, but no values changed.";

        userRepo.save(user);

        saveAuditLog(
                user,
                "USER_UPDATED",
                "Admin updated user " + user.getUsername() + ". " + changes.toString(),
                adminUsername
        );

        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }


    @Override
    @Transactional
    public void softDeleteUser(Long userId) {
        String adminUsername = getCurrentAdminUsername();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setActive(false);
        userRepo.save(user);

        saveAuditLog(
                user,
                "USER_SOFT_DELETE",
                "User '" + user.getUsername() + "' was soft-deleted (deactivated) by admin: " + adminUsername,
                adminUsername
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getUserAuditHistory(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<AuditLog> logs = auditLogRepo.findHistoryByUserId(userId);


        return logs.stream()
                .map(log -> new AuditLogResponseDTO(
                        log.getId(),
                        log.getAction(),
                        log.getDescription(),
                        log.getPerformedBy(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    private String getCurrentAdminUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void saveAuditLog(UserEntity user, String action, String description, String admin) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType("USER");
        log.setUserId(user);
        log.setEntityId(user.getId());
        log.setDescription(description);
        log.setPerformedBy(admin);
        log.setCreatedAt(LocalDateTime.now());

        auditLogRepo.save(log);
    }



}
