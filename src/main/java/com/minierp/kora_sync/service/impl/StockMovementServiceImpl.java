package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.StockMovementRequest;
import com.minierp.kora_sync.dto.StockMovementResponse;
import com.minierp.kora_sync.enums.MovementType;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.InvalidInputException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.StockMovementMapper;
import com.minierp.kora_sync.model.Product;
import com.minierp.kora_sync.model.StockMovement;
import com.minierp.kora_sync.repository.ProductRepository;
import com.minierp.kora_sync.repository.StockMovementRepository;
import com.minierp.kora_sync.security.CurrentUser;
import com.minierp.kora_sync.service.StockMovementService;
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
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final StockMovementMapper stockMovementMapper;
    private final CurrentUser currentUser;

    @Override
    @Transactional
    public StockMovementResponse register(UUID companyId, UUID productId, StockMovementRequest request) {
        Product product = productRepository.findByIdAndCompanyIdForUpdate(productId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.isActive()) {
            throw new BusinessConflictException("Product is inactive; stock movements are not allowed");
        }

        validate(product, request);

        BigDecimal current = product.getStockQuantity();
        BigDecimal newBalance = switch (request.movementType()) {
            case ENTRY -> current.add(request.quantity());
            case EXIT -> current.subtract(request.quantity());
            case ADJUSTMENT -> request.quantity();
        };

        if (newBalance.signum() < 0) {
            throw new BusinessConflictException("Insufficient stock for product %s: available %s, requested %s"
                    .formatted(product.getSku(), current.toPlainString(), request.quantity().toPlainString()));
        }

        product.setStockQuantity(newBalance);

        StockMovement movement = new StockMovement();
        movement.setCompanyId(companyId);
        movement.setProductId(productId);
        movement.setMovementType(request.movementType());
        movement.setQuantity(newBalance.subtract(current));
        movement.setBalanceAfter(newBalance);
        movement.setReason(request.reason());
        movement.setCreatedBy(currentUser.get().userId());

        StockMovement saved = stockMovementRepository.save(movement);

        log.info("Stock movement registered: id={} productId={} companyId={} type={} balanceAfter={}",
                saved.getId(), productId, companyId, request.movementType(), newBalance);
        return stockMovementMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockMovementResponse> findAll(UUID companyId, UUID productId, MovementType movementType, Pageable pageable) {
        if (!productRepository.existsByIdAndCompanyId(productId, companyId)) {
            throw new ResourceNotFoundException("Product", productId);
        }

        Page<StockMovementResponse> page = stockMovementRepository
                .findAll(buildFilters(companyId, productId, movementType), pageable)
                .map(stockMovementMapper::toResponse);

        return PageResponse.from(page);
    }

    private void validate(Product product, StockMovementRequest request) {
        MovementType type = request.movementType();
        BigDecimal quantity = request.quantity();

        if (type.isReasonRequired() && request.reason() == null) {
            throw new InvalidInputException("A reason is required for " + type + " movements");
        }
        if (type != MovementType.ADJUSTMENT && quantity.signum() == 0) {
            throw new InvalidInputException("Quantity must be greater than zero for " + type + " movements");
        }
        if (!product.getUnit().allowsFractions() && quantity.stripTrailingZeros().scale() > 0) {
            throw new InvalidInputException("Unit " + product.getUnit() + " only accepts whole quantities");
        }
    }

    private Specification<StockMovement> buildFilters(UUID companyId, UUID productId, MovementType movementType) {
        Specification<StockMovement> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("companyId"), companyId),
                cb.equal(root.get("productId"), productId));
        if (movementType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("movementType"), movementType));
        }
        return spec;
    }
}
