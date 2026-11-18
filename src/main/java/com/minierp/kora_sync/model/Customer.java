package com.minierp.kora_sync.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends BusinessPartner {
}
