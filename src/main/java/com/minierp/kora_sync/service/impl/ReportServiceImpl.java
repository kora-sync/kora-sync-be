package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.ProductFilter;
import com.minierp.kora_sync.dto.ProductResponse;
import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.enums.ExportFormat;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.export.PagedExport;
import com.minierp.kora_sync.export.TabularWriter;
import com.minierp.kora_sync.export.TabularWriters;
import com.minierp.kora_sync.service.ProductService;
import com.minierp.kora_sync.service.ReportService;
import com.minierp.kora_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final List<String> USER_COLUMNS = List.of("Name", "E-mail", "Role", "Active", "Created at");

    private static final List<String> PRODUCT_COLUMNS = List.of("SKU", "Name", "Unit", "Cost price", "Sale price",
            "Stock quantity", "Minimum stock", "Stock value", "Low stock", "Active");

    private final UserService userService;
    private final ProductService productService;
    private final TabularWriters tabularWriters;

    @Override
    public void exportUsers(UUID companyId, String name, Role role, Boolean active,
                            ExportFormat format, OutputStream out) throws IOException {
        try (TabularWriter writer = tabularWriters.open(format, out)) {
            PagedExport.write(writer, USER_COLUMNS, Sort.by("name", "id"),
                    pageable -> userService.findAll(companyId, name, role, active, pageable).data(),
                    ReportServiceImpl::userRow);
        }
    }

    @Override
    public void exportProducts(UUID companyId, ProductFilter filter,
                               ExportFormat format, OutputStream out) throws IOException {
        try (TabularWriter writer = tabularWriters.open(format, out)) {
            PagedExport.write(writer, PRODUCT_COLUMNS, Sort.by("name", "id"),
                    pageable -> productService.findAll(companyId, filter, pageable).data(),
                    ReportServiceImpl::productRow);
        }
    }

    private static List<?> productRow(ProductResponse product) {
        // Stock value = what the stock on hand cost us.
        BigDecimal stockValue = product.costPrice().multiply(product.stockQuantity());
        return Arrays.asList(product.sku(), product.name(), product.unit(), product.costPrice(),
                product.salePrice(), product.stockQuantity(), product.minimumStock(), stockValue,
                product.lowStock(), product.active());
    }

    private static List<?> userRow(UserResponse user) {
        // Arrays.asList on purpose: List.of does not accept null cells.
        return Arrays.asList(user.name(), user.email(), user.role(), user.active(), user.createdAt());
    }
}