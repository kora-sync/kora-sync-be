package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SettlementRequest;
import com.minierp.kora_sync.dto.SettlementResponse;
import com.minierp.kora_sync.enums.PaymentMethod;
import com.minierp.kora_sync.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/financial-entries/{entryId}/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlements",
        description = "Settle (pay or receive) financial entries, in part or in full, and see the history")
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a settlement",
            description = "Settles part or all of the remaining amount of an OPEN or PARTIALLY_SETTLED entry. "
                    + "The entry becomes PARTIALLY_SETTLED, or SETTLED when nothing remains. The amount cannot "
                    + "exceed the remaining balance and the settlement date cannot be in the future "
                    + "(it defaults to today). Settlements are never changed afterwards.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Settlement registered"),
            @ApiResponse(responseCode = "404", description = "Financial entry not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "The entry does not accept settlements "
                    + "(SETTLED or CANCELED) or the amount exceeds the remaining balance",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public SettlementResponse register(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Financial entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
            @PathVariable UUID entryId,
            @Valid @RequestBody SettlementRequest request) {
        return settlementService.register(companyId, entryId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List settlements of an entry",
            description = "Returns a page of the settlements of the entry, most recent settlement date first by "
                    + "default. Use page (zero-based), size (max 100) and sort (e.g. settledOn,asc). "
                    + "The optional paymentMethod filter narrows the history to one payment method.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of settlements (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Financial entry not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<SettlementResponse> list(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Financial entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
            @PathVariable UUID entryId,
            @Parameter(description = "Return only settlements paid this way. Omit to return all", example = "PIX")
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @ParameterObject
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "settledOn", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)})
            Pageable pageable) {
        return settlementService.findAll(companyId, entryId, paymentMethod, pageable);
    }
}