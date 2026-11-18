package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.CompanyResponse;
import com.minierp.kora_sync.dto.CustomerRequest;
import com.minierp.kora_sync.dto.CustomerResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.CustomerMapper;
import com.minierp.kora_sync.model.Customer;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.CustomerRepository;
import com.minierp.kora_sync.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse create(UUID companyId, CustomerRequest request) {
        requireCompany(companyId);

        if (request.document() != null && customerRepository.existsByCompanyIdAndDocument(companyId, request.document())) {
            throw new BusinessConflictException("Document already registered fot this company: " + request.document());
        }

        Customer customer = customerMapper.toEntity(request);

        customer.setCompanyId(companyId);

        Customer saved = customerRepository.save(customer);

        log.info("Customer created: id={} companyId={}", saved.getId(), companyId);

        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> findAll(UUID companyId, String name, Boolean active, Pageable pageable) {
        requireCompany(companyId);
        Page<CustomerResponse> page = customerRepository
                .findAll(buildFilters(companyId, name, active), pageable)
                .map(customerMapper::toResponse);

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findGetById(UUID companyId, UUID id) {
        return customerMapper.toResponse(findOrThrow(companyId, id));
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID companyId, UUID id, CustomerRequest request) {
        Customer customer = findOrThrow(companyId, id);

        if (request.document() != null && customerRepository.existsByCompanyIdAndDocumentAndIdNot(companyId, request.document(), id)) {
            throw new BusinessConflictException("Document already registered for this company: " + request.document());
        }

        customerMapper.updateEntity(request, customer);

        Customer saved = customerRepository.saveAndFlush(customer);

        log.info("Customer updated: id={} companyId={}", id, companyId);

        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivate(UUID companyId, UUID id) {
        Customer customer = findOrThrow(companyId, id);

        customer.setActive(false);

        log.info("Customer deactivated: id={} companyId={}", id, companyId);
    }

    private void requireCompany(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
    }

    private Customer findOrThrow(UUID companyId, UUID id) {
        return customerRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    private Specification<Customer> buildFilters(UUID companyId, String name, Boolean active) {
        Specification<Customer> spec = (root, query, cb) -> cb.equal(root.get("companyId"), companyId);
        if (name != null && !name.isBlank()) {
            String pattern = "%" + name.strip().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }
        return spec;
    }
}
