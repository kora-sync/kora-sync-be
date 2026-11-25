package com.minierp.kora_sync.model;

import com.minierp.kora_sync.enums.MovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Immutable
@Entity
@Table(name = "stock_movements")
public class StockMovement extends BaseEntity {
    @Column(name = "company_id", nullable = false, updatable = false)
    private UUID companyId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;

    @Column(name = "balance_after", nullable = false, precision = 14, scale = 3)
    private BigDecimal balanceAfter;

    @Column(length = 255)
    private String reason;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
}
