package com.minierp.kora_sync.export;

import com.minierp.kora_sync.enums.ExportFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;

@Component
@RequiredArgsConstructor
public class TabularWriters {

    private final Clock clock;

    public TabularWriter open(ExportFormat format, OutputStream out) throws IOException {
        return switch (format) {
            case CSV -> new CsvTabularWriter(out, clock.getZone());
            case XLSX -> new XlsxTabularWriter(out, clock.getZone());
        };
    }
}