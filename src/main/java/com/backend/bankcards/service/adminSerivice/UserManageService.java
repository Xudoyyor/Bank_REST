package com.backend.bankcards.service.adminSerivice;

import com.backend.bankcards.dto.AuditLogResponseDTO;
import com.backend.bankcards.dto.cardsDTO.CardResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserResponseDTO;
import com.backend.bankcards.dto.usersDTO.UserSearchFilter;
import com.backend.bankcards.dto.usersDTO.UserUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserManageService {


    void blockUser(Long userId);

    void activateUser(Long userId);

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO updateUser(Long userId, UserUpdateDTO updateRequest);

    void softDeleteUser(Long userId);

    List<AuditLogResponseDTO> getUserAuditHistory(Long userId);

    Page<UserResponseDTO> searchUsers(UserSearchFilter filter);
    List<CardResponseDTO> getUserCards(Long userId);
}
