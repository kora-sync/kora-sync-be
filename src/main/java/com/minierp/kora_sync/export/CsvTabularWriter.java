package com.minierp.kora_sync.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CSV for Excel with Brazilian settings: UTF-8 with BOM, ';' separator, decimal comma and dd/MM/yyyy dates.
 */
public final class CsvTabularWriter implements TabularWriter {

    private static final char SEPARATOR = ';';
    private static final String LINE_END = "\r\n";
    private static final String FORMULA_TRIGGERS = "=+-@\t\r";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Writer writer;
    private final ZoneId zone;

    public CsvTabularWriter(OutputStream out, ZoneId zone) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        this.zone = zone;
        writer.write('\uFEFF'); // BOM: makes Excel read the file as UTF-8
    }

    @Override
    public void header(List<String> columns) throws IOException {
        List<String> cells = new ArrayList<>(columns.size());
        for (String column : columns) {
            cells.add(escape(column));
        }
        writeLine(cells);
    }

    @Override
    public void row(List<?> values) throws IOException {
        List<String> cells = new ArrayList<>(values.size());
        for (Object value : values) {
            cells.add(format(value));
        }
        writeLine(cells);
    }

    @Override
    public void close() throws IOException {
        writer.flush();
    }

    private void writeLine(List<String> cells) throws IOException {
        writer.write(String.join(String.valueOf(SEPARATOR), cells));
        writer.write(LINE_END);
    }

    private String format(Object value) {
        return switch (value) {
            case null -> "";
            case String text -> escape(neutralizeFormula(text));
            case BigDecimal number -> number.toPlainString().replace('.', ',');
            case Number number -> number.toString();
            case LocalDate date -> DATE.format(date);
            case Instant instant -> DATE_TIME.format(instant.atZone(zone));
            case Boolean flag -> flag ? "Yes" : "No";
            case Enum<?> constant -> constant.name();
            case UUID id -> id.toString();
            default -> throw new IllegalArgumentException(
                    "Unsupported cell type: " + value.getClass().getName());
        };
    }

    /**
     * A text starting with = + - @ is read by spreadsheets as a formula (CSV injection).
     * The leading apostrophe makes it plain text.
     */
    private String neutralizeFormula(String text) {
        return !text.isEmpty() && FORMULA_TRIGGERS.indexOf(text.charAt(0)) >= 0 ? "'" + text : text;
    }

    /**
     * RFC 4180: a field with the separator, quotes or line breaks is wrapped in quotes, and quotes are doubled.
     */
    private String escape(String text) {
        boolean mustQuote = text.indexOf(SEPARATOR) >= 0 || text.indexOf('"') >= 0
                || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0;
        return mustQuote ? '"' + text.replace("\"", "\"\"") + '"' : text;
    }
}