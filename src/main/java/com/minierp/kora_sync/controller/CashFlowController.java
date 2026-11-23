package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.ForecastCashFlowResponse;
import com.minierp.kora_sync.dto.RealizedCashFlowResponse;
import com.minierp.kora_sync.service.CashFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/cash-flow")
@RequiredArgsConstructor
@Tag(name = "Cash Flow",
        description = "See the money that moved (realized) and the money still expected (forecast) by day")
public class CashFlowController {

    private final CashFlowService cashFlowService;

    @GetMapping("/realized")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the realized cash flow",
            description = "Returns, for the period, the money that actually moved: settlements grouped by their "
                    + "settlement date. Settlements of receivables are inflows and settlements of payables are "
                    + "outflows. The response has one item per day (days without movement come as zero), the "
                    + "period totals and the totals per payment method. The running balance starts at zero on the "
                    + "first day because there is no opening balance. The period is limited to 366 days.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Realized cash flow of the period"),
            @ApiResponse(responseCode = "400", description = "Invalid period: a date is missing or malformed, "
                    + "'from' is after 'to', or the period exceeds 366 days",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RealizedCashFlowResponse realized(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "First day of the period (inclusive), yyyy-MM-dd", example = "2026-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Last day of the period (inclusive), yyyy-MM-dd", example = "2026-11-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return cashFlowService.realized(companyId, from, to);
    }

    @GetMapping("/forecast")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the forecast cash flow",
            description = "Returns, for the period, what is still expected: the remaining amount of OPEN and "
                    + "PARTIALLY_SETTLED entries grouped by due date. Receivables are expected inflows and "
                    + "payables are expected outflows. Settled and canceled entries are not counted, and entries "
                    + "due before the period are not included. The period is limited to 366 days.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forecast cash flow of the period"),
            @ApiResponse(responseCode = "400", description = "Invalid period: a date is missing or malformed, "
                    + "'from' is after 'to', or the period exceeds 366 days",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ForecastCashFlowResponse forecast(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "First day of the period (inclusive), yyyy-MM-dd", example = "2026-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Last day of the period (inclusive), yyyy-MM-dd", example = "2026-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return cashFlowService.forecast(companyId, from, to);
    }
}