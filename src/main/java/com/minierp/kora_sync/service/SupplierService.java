package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SupplierRequest;
import com.minierp.kora_sync.dto.SupplierResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupplierService {
    SupplierResponse create(UUID companyId, SupplierRequest request);
    PageResponse<SupplierResponse> findAll(UUID companyId, String name, Boolean active, Pageable pageable);
    SupplierResponse findGetById(UUID companyId, UUID id);
    SupplierResponse update(UUID companyId, UUID id, SupplierRequest request);
    void deactivate(UUID companyId, UUID id);
}
