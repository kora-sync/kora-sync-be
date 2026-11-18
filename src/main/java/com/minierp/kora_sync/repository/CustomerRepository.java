package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByIdAndCompanyId(UUID id, UUID companyId);
    boolean existsByCompanyIdAndDocument(UUID companyId, String document);
    boolean existsByCompanyIdAndDocumentAndIdNot(UUID companyId, String document, UUID id);
}
