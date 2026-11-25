package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.StockMovementRequest;
import com.minierp.kora_sync.dto.StockMovementResponse;
import com.minierp.kora_sync.enums.MovementType;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/products/{productId}/stock-movements")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Stock Movements",
        description = "Register stock entries, exits and inventory adjustments and see the history of a product")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or !#request.movementType().isRestrictedToManagers()")
    @Operation(
            summary = "Register a stock movement",
            description = "Registers an ENTRY, EXIT or ADJUSTMENT for the product and updates its balance "
                    + "atomically. ENTRY and EXIT require a quantity greater than zero. For ADJUSTMENT the "
                    + "quantity is the counted balance and a reason is required. An EXIT larger than the "
                    + "current balance is refused. Products in UN, CX or PCT accept whole quantities only. "
                    + "Inactive products do not accept movements. ENTRY and EXIT: any signed-in user. "
                    + "ADJUSTMENT: MANAGER or ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movement registered; the response shows the "
                    + "resulting balance"),
            @ApiResponse(responseCode = "404", description = "Product not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient stock, or the product is inactive",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public StockMovementResponse register(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
            @PathVariable UUID productId,
            @Valid @RequestBody StockMovementRequest request) {
        return stockMovementService.register(companyId, productId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List stock movements",
            description = "Returns a page of the movement history of the product, newest first by default. "
                    + "Use page (zero-based), size (max 100) and sort (e.g. createdAt,asc). "
                    + "The optional movementType filter narrows the history to one kind of movement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of movements (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Product not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<StockMovementResponse> findAll(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
            @PathVariable UUID productId,
            @Parameter(description = "Return only movements of this kind. Omit to return all kinds",
                    example = "EXIT")
            @RequestParam(required = false) MovementType movementType,
            @ParameterObject @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return stockMovementService.findAll(companyId, productId, movementType, pageable);
    }
}