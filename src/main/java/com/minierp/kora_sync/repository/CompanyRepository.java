package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

    boolean existsByTaxId(String taxId);
    boolean existsByTaxIdAndIdNot(String taxId, UUID id);
}
