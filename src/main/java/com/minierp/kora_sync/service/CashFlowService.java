package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.ForecastCashFlowResponse;
import com.minierp.kora_sync.dto.RealizedCashFlowResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface CashFlowService {
    RealizedCashFlowResponse realized(UUID companyId, LocalDate from, LocalDate to);
    ForecastCashFlowResponse forecast(UUID companyId, LocalDate from, LocalDate to);
}
