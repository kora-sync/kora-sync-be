package com.minierp.kora_sync.export;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Writes a report as an .xlsx file using POI's streaming API (SXSSF):
 * only a small window of rows stays in memory, the rest goes to a temp file.
 * Values keep their real types (numbers, dates), so Excel can sort and sum them.
 */
public class XlsxTabularWriter implements TabularWriter {

    /** Excel's hard limit is 1,048,576 rows per sheet, and one is the header. */
    private static final int MAX_DATA_ROWS = 1_048_575;
    /** Excel's hard limit for the text inside one cell. */
    private static final int MAX_CELL_TEXT = 32_767;
    private static final int ROW_WINDOW = 100;

    private final OutputStream out;
    private final ZoneId zone;
    private final SXSSFWorkbook workbook = new SXSSFWorkbook(ROW_WINDOW);
    private final SXSSFSheet sheet;
    private final CellStyle headerStyle;
    private final CellStyle dateStyle;
    private final CellStyle dateTimeStyle;
    private final CellStyle numberStyle;
    private int nextRow = 0;

    public XlsxTabularWriter(OutputStream out, ZoneId zone) {
        // POI may close the stream when it finishes; the container owns it, so we shield it.
        this.out = new NonClosingOutputStream(out);
        this.zone = zone;
        this.workbook.setCompressTempFiles(true);
        this.sheet = workbook.createSheet("Report");

        var dataFormat = workbook.createDataFormat();

        Font bold = workbook.createFont();
        bold.setBold(true);
        this.headerStyle = workbook.createCellStyle();
        this.headerStyle.setFont(bold);

        this.dateStyle = workbook.createCellStyle();
        this.dateStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy"));

        this.dateTimeStyle = workbook.createCellStyle();
        this.dateTimeStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy hh:mm"));

        this.numberStyle = workbook.createCellStyle();
        this.numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00##"));
    }

    @Override
    public void header(List<String> columns) {
        Row row = sheet.createRow(nextRow++);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerStyle);
            // Width from the header text: auto-size would force POI to track every row.
            int chars = Math.min(Math.max(columns.get(i).length() + 4, 14), 50);
            sheet.setColumnWidth(i, chars * 256);
        }
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, columns.size() - 1));
    }

    @Override
    public void row(List<?> values) {
        if (nextRow > MAX_DATA_ROWS) {
            throw new IllegalStateException(
                    "Excel supports at most " + MAX_DATA_ROWS + " data rows per sheet. Use the CSV format for this report.");
        }
        Row row = sheet.createRow(nextRow++);
        for (int i = 0; i < values.size(); i++) {
            write(row.createCell(i), values.get(i));
        }
    }

    private void write(Cell cell, Object value) {
        switch (value) {
            case null -> { /* leaves the cell blank */ }
            case Boolean flag -> cell.setCellValue(flag ? "Yes" : "No");
            case BigDecimal number -> {
                cell.setCellValue(number.doubleValue());
                cell.setCellStyle(numberStyle);
            }
            case Number number -> cell.setCellValue(number.doubleValue());
            case LocalDate date -> {
                cell.setCellValue(DateUtil.getExcelDate(date));
                cell.setCellStyle(dateStyle);
            }
            case LocalDateTime dateTime -> {
                cell.setCellValue(DateUtil.getExcelDate(dateTime));
                cell.setCellStyle(dateTimeStyle);
            }
            case Instant instant -> {
                cell.setCellValue(DateUtil.getExcelDate(LocalDateTime.ofInstant(instant, zone)));
                cell.setCellStyle(dateTimeStyle);
            }
            case Enum<?> constant -> cell.setCellValue(constant.name());
            default -> cell.setCellValue(limit(value.toString()));
        }
    }

    private static String limit(String text) {
        return text.length() <= MAX_CELL_TEXT ? text : text.substring(0, MAX_CELL_TEXT);
    }

    @Override
    public void close() throws IOException {
        try {
            workbook.write(out);
            out.flush();
        } finally {
            workbook.dispose(); // deletes the temp files
            workbook.close();
        }
    }

    private static final class NonClosingOutputStream extends FilterOutputStream {

        NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            out.write(bytes, offset, length);
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}