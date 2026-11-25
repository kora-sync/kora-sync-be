package com.minierp.kora_sync.service;

import com.minierp.kora_sync.enums.ExportFormat;
import com.minierp.kora_sync.enums.Role;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public interface ReportService {

    void exportUsers(UUID companyId, String name, Role role, Boolean active,
                     ExportFormat format, OutputStream out) throws IOException;
}