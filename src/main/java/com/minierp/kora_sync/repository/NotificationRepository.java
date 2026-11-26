package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByCompanyId(UUID companyId, Pageable pageable);

    Page<Notification> findByCompanyIdAndReadAtIsNull(UUID companyId, Pageable pageable);

    long countByCompanyIdAndReadAtIsNull(UUID companyId);

    Optional<Notification> findByIdAndCompanyId(UUID id, UUID companyId);

    // A bulk UPDATE skips @PreUpdate, so updatedAt is set by hand.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Notification n
               set n.readAt = :now, n.readBy = :userId, n.updatedAt = :now
             where n.companyId = :companyId and n.readAt is null
            """)
    int markAllRead(@Param("companyId") UUID companyId,
                    @Param("userId") UUID userId,
                    @Param("now") Instant now);
}