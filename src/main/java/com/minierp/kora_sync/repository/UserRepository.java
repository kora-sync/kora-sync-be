package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndCompanyId(UUID id, UUID companyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id and u.companyId = :companyId")
    Optional<User> findByIdAndCompanyIdForUpdate(@Param("id") UUID id, @Param("companyId") UUID companyId);

    long countByCompanyIdAndRoleAndActiveTrue(UUID companyId, Role role);
}