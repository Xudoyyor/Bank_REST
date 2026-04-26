package com.backend.bankcards.repository;

import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(:query IS NULL OR " +
            "LOWER(CAST(u.username AS string)) LIKE LOWER(CAST(CONCAT('%', :query, '%') AS string)) OR " +
            "LOWER(CAST(u.firstName AS string)) LIKE LOWER(CAST(CONCAT('%', :query, '%') AS string)) OR " +
            "LOWER(CAST(u.lastName AS string)) LIKE LOWER(CAST(CONCAT('%', :query, '%') AS string))) AND " +
            "(:email IS NULL OR u.email = :email) AND " +
            "(:phone IS NULL OR u.phone = :phone) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<UserEntity> searchByFilter(
            @Param("query") String query,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("role") Role role,
            @Param("isActive") Boolean isActive,
            Pageable pageable);


    Optional<UserEntity> findByUsername(String username);
}
