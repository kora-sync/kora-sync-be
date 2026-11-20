package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.ProductFilter;
import com.minierp.kora_sync.dto.ProductRequest;
import com.minierp.kora_sync.dto.ProductResponse;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.ProductMapper;
import com.minierp.kora_sync.model.Product;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.ProductRepository;
import com.minierp.kora_sync.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(UUID companyId, ProductRequest request) {
        requireCompany(companyId);

        if (productRepository.existsByCompanyIdAndSku(companyId, request.sku())) {
            throw new BusinessConflictException("SKU already registered for this company");
        }

        Product product = productMapper.toEntity(request);

        product.setCompanyId(companyId);

        Product saved = productRepository.save(product);

        log.info("Product created id={} companyId={}", saved.getId(), companyId);

        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(UUID companyId, ProductFilter filter, Pageable pageable) {
        requireCompany(companyId);

        Page<ProductResponse> page = productRepository
                .findAll(buildFilters(companyId, filter), pageable)
                .map(productMapper::toResponse);

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findGetById(UUID companyId, UUID id) {
        return productMapper.toResponse(findOrThrow(companyId, id));
    }

    @Override
    @Transactional
    public ProductResponse update(UUID companyId, UUID id, ProductRequest request) {
       Product product = findForUpdateOrThrow(companyId, id);

       if (productRepository.existsByCompanyIdAndSkuAndIdNot(companyId, request.sku(), id)) {
           throw new BusinessConflictException("SKU already registered for this company: " + request.sku());
       }

       productMapper.updateEntity(request, product);

       Product saved = productRepository.saveAndFlush(product);

       log.info("Product updated: id={} companyId={}", id, companyId);

       return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivate(UUID companyId, UUID id) {
        Product product = findForUpdateOrThrow(companyId, id);
        product.setActive(false);
        log.info("Product deactivated: id={} companyId={}", id, companyId);
    }

    private void requireCompany(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
    }

    private Product findOrThrow(UUID companyId, UUID id) {
        return productRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Product findForUpdateOrThrow(UUID companyId, UUID id) {
        return productRepository.findByIdAndCompanyIdForUpdate(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Specification<Product> buildFilters(UUID companyId, ProductFilter filter) {
        Specification<Product> spec = (root, query, cb) -> cb.equal(root.get("companyId"), companyId);
        if (filter.name() != null && !filter.name().isBlank()) {
            String pattern = "%" + filter.name().strip().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        if (filter.sku() != null && !filter.sku().isBlank()) {
            String pattern = "%" + filter.sku().strip().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(root.get("sku"), pattern));
        }
        if (filter.active() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), filter.active()));
        }
        if (Boolean.TRUE.equals(filter.lowStock())) {
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.greaterThan(root.<BigDecimal>get("minimumStock"), BigDecimal.ZERO),
                    cb.lessThanOrEqualTo(root.<BigDecimal>get("stockQuantity"),
                            root.<BigDecimal>get("minimumStock"))));
        }
        return spec;
    }
}
