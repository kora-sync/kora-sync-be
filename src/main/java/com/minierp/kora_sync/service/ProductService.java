package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.ProductFilter;
import com.minierp.kora_sync.dto.ProductRequest;
import com.minierp.kora_sync.dto.ProductResponse;
import com.minierp.kora_sync.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    ProductResponse create(UUID companyId, ProductRequest request);
    PageResponse<ProductResponse> findAll(UUID companyId, ProductFilter filter, Pageable pageable);
    ProductResponse findGetById(UUID companyId, UUID id);
    ProductResponse update(UUID companyId, UUID id, ProductRequest request);
    void deactivate(UUID companyId, UUID id);
}
