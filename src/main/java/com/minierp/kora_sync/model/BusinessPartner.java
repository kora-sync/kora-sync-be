package com.minierp.kora_sync.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BusinessPartner extends BaseEntity {
    @Column(name = "company_id", nullable = false, updatable = false)
    private UUID companyId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 14)
    private String document;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private boolean active = true;
}
