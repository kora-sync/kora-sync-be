package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndCompanyId(UUID id, UUID companyId);
    boolean existsByCompanyIdAndSku(UUID companyId, String sku);
    boolean existsByCompanyIdAndSkuAndIdNot(UUID companyId, String sku, UUID id);
}
