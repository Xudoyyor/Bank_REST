package com.backend.bankcards.service.adminSerivice;

import com.backend.bankcards.dto.UserSearchFilter;
import com.backend.bankcards.entity.AuditLog;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.repository.AuditLogRepository;
import com.backend.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserManageService {

    private final UserRepository userRepo;
    private final AuditLogRepository auditLogRepo;

    UserManageService(UserRepository userRepo, AuditLogRepository auditLogRepo){
        this.userRepo = userRepo;
        this.auditLogRepo = auditLogRepo;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    public Page<UserEntity> searchUsers(UserSearchFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());


        return userRepo.searchByFilter(
                filter.query(),
                filter.email(),
                filter.phone(),
                filter.role(),
                filter.isActive(),
                pageable
        );
    }



    public void blockUser(Long userId) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(true);
        user.setActive(false);
        userRepo.save(user);


        AuditLog log = new AuditLog();
        log.setAction("USER_BLOCKED");
        log.setEntityType("USER");
        log.setUserId(user);
        log.setEntityId(userId);
        log.setPerformedBy(adminUsername);

        auditLogRepo.save(log);
    }


    public void activateUser(Long userId) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(false);
        user.setActive(true);
        userRepo.save(user);


        AuditLog log = new AuditLog();
        log.setAction("USER_ACTIVATED");
        log.setEntityType("USER");
        log.setUserId(user);
        log.setEntityId(userId);
        log.setPerformedBy(adminUsername);

        auditLogRepo.save(log);
    }


    public UserEntity getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
    }




    public UserEntity updateUser(Long userId, UserEntity updateRequest) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPhone(updateRequest.getPhone());


        if (updateRequest.getRole() != null) {
            user.setRole(updateRequest.getRole());
        }

        UserEntity savedUser = userRepo.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_UPDATED");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setUserId(user);
        auditLog.setPerformedBy(adminUsername);

        auditLogRepo.save(auditLog);

        return savedUser;
    }



    @Transactional
    public void softDeleteUser(Long userId) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);

        userRepo.save(user);

        // Audit Log
        AuditLog log = new AuditLog();
        log.setAction("USER_SOFT_DELETE");
        log.setEntityType("USER");
        log.setEntityId(userId);
        log.setUserId(user);
        log.setPerformedBy(adminUsername);

        auditLogRepo.save(log);
    }


}
