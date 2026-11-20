package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.FinancialEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FinancialEntryRepository extends JpaRepository<FinancialEntry, UUID>, JpaSpecificationExecutor<FinancialEntry> {
    Optional<FinancialEntry> findByIdAndCompanyId(UUID id, UUID companyId);
}
