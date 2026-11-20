package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndCompanyId(UUID id, UUID companyId);
    boolean existsByCompanyIdAndSku(UUID companyId, String sku);
    boolean existsByCompanyIdAndSkuAndIdNot(UUID companyId, String sku, UUID id);

    boolean existsByIdAndCompanyId(UUID id, UUID companyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id and p.companyId = :companyId")
    Optional<Product> findByIdAndCompanyIdForUpdate(@Param("id") UUID id, @Param("companyId") UUID companyId);
}
