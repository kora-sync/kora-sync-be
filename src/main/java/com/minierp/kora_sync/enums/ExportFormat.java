package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "File format of an export. CSV opens directly in Excel (Brazilian settings): "
        + "UTF-8, ';' separator, decimal comma")
public enum ExportFormat {
    CSV("csv", "text/csv; charset=UTF-8");

    private final String extension;
    private final String contentType;

    ExportFormat(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String extension() {
        return extension;
    }

    public String contentType() {
        return contentType;
    }
}
