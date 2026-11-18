package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.CustomerRequest;
import com.minierp.kora_sync.dto.CustomerResponse;
import com.minierp.kora_sync.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(UUID companyId, CustomerRequest request);
    PageResponse<CustomerResponse> findAll(UUID companyId, String name, Boolean active, Pageable pageable);
    CustomerResponse findGetById(UUID companyId, UUID id);
    CustomerResponse update(UUID companyId, UUID id, CustomerRequest request);
    void deactivate(UUID companyId, UUID id);
}
