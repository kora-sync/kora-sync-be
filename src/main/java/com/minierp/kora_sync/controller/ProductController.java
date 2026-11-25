package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.ProductFilter;
import com.minierp.kora_sync.dto.ProductRequest;
import com.minierp.kora_sync.dto.ProductResponse;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.security.RequiresManager;
import com.minierp.kora_sync.service.ProductService;
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
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Register and manage the products of a company and see their stock balance")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresManager
    @Operation(
            summary = "Create a product",
            description = "Creates a product for the given company. The SKU must be unique within the company. "
                    + "New products are created active with a zero stock balance; to add stock, register an "
                    + "ENTRY stock movement.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "SKU already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse create(
            @CurrentCompany UUID companyId,
            @Valid @RequestBody ProductRequest request) {
        return productService.create(companyId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List products",
            description = "Returns a page of products of the company. All filters are optional and can be "
                    + "combined. Use page (zero-based), size (max 100) and sort (e.g. name,asc). "
                    + "When no sort is given, results are ordered by name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<ProductResponse> list(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Case-insensitive partial match on the product name", example = "pão")
            @RequestParam(required = false) String name,
            @Parameter(description = "Partial match on the SKU (case-insensitive)", example = "pao-")
            @RequestParam(required = false) String sku,
            @Parameter(description = "Filter by active flag. Omit to return both active and inactive products",
                    example = "true")
            @RequestParam(required = false) Boolean active,
            @Parameter(description = "When true, returns only products with a minimum configured whose balance "
                    + "is at or below it. When false or omitted, this filter is not applied", example = "true")
            @RequestParam(required = false) Boolean lowStock,
            @ParameterObject @SortDefault(sort = "name") Pageable pageable) {
        return productService.findAll(companyId, new ProductFilter(name, sku, active, lowStock), pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a product",
            description = "Returns the product with the given identifier, active or not, including its current "
                    + "stock balance. Products of other companies are never returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse getById(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
            @PathVariable UUID id) {
        return productService.findGetById(companyId, id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RequiresManager
    @Operation(
            summary = "Update a product",
            description = "Replaces all editable fields of the product. The SKU must remain unique within the "
                    + "company. The stock balance and the active flag are not changed by this operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "SKU already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse update(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return productService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresManager
    @Operation(
            summary = "Deactivate a product",
            description = "Marks the product as inactive. The record, its stock balance and its movement history "
                    + "are kept, so the product is not physically deleted. Deactivating an already inactive "
                    + "product has no effect.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deactivated"),
            @ApiResponse(responseCode = "404", description = "Product not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deactivate(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
            @PathVariable UUID id) {
        productService.deactivate(companyId, id);
    }
}