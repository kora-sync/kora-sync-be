package com.minierp.kora_sync.model;

import com.minierp.kora_sync.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Immutable
@Entity
@Table(name = "settlements")
public class Settlement extends BaseEntity {

    @Column(name = "company_id", nullable = false, updatable = false)
    private UUID companyId;

    @Column(name = "entry_id", nullable = false, updatable = false)
    private UUID entryId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "settled_on", nullable = false)
    private LocalDate settledOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
}