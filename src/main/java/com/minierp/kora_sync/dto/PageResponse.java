package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Paginated response envelope")
public record PageResponse<T>(

        @Schema(description = "Items of the current page")
        List<T> data,

        @Schema(description = "Pagination metadata")
        PageInfo page
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new PageInfo(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @Schema(description = "Pagination metadata")
    public record PageInfo(

            @Schema(description = "Zero-based page number", example = "0")
            int number,

            @Schema(description = "Page size", example = "10")
            int size,

            @Schema(description = "Total number of items across all pages", example = "42")
            long totalElements,

            @Schema(description = "Total number of pages", example = "5")
            int totalPages
    ) {
    }
}