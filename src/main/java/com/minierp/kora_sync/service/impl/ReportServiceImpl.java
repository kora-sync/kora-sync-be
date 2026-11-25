package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.enums.ExportFormat;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.export.PagedExport;
import com.minierp.kora_sync.export.TabularWriter;
import com.minierp.kora_sync.export.TabularWriters;
import com.minierp.kora_sync.service.ReportService;
import com.minierp.kora_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final List<String> USER_COLUMNS = List.of("Name", "E-mail", "Role", "Active", "Created at");

    private final UserService userService;
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

    private static List<?> userRow(UserResponse user) {
        // Arrays.asList on purpose: List.of does not accept null cells.
        return Arrays.asList(user.name(), user.email(), user.role(), user.active(), user.createdAt());
    }
}