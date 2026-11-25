package com.minierp.kora_sync.export;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Writes a table (a header and rows) to an output stream, one row at a time.
 * Each format (CSV, XLSX) has its own implementation, so reports do not know the file format.
 */
public interface TabularWriter extends Closeable {

    void header(List<String> columns) throws IOException;

    /**
     * Values may be String, Number, LocalDate, Instant, Boolean, Enum, UUID or null.
     */
    void row(List<?> values) throws IOException;

}
