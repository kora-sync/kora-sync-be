package com.minierp.kora_sync.export;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Reads a listing page by page and writes each row as soon as it is read, so memory stays constant
 * no matter how many rows the report has. Each page is a short transaction of its own.
 */
public final class PagedExport {

    public static final int PAGE_SIZE = 500;

    private PagedExport() {
    }

    public static <T> void write(TabularWriter writer,
                                 List<String> columns,
                                 Sort sort,
                                 Function<Pageable, List<T>> pageFetcher,
                                 Function<T, List<?>> rowMapper) throws IOException {
        writer.header(columns);
        for (int page = 0; ; page++) {
            List<T> rows = pageFetcher.apply(PageRequest.of(page, PAGE_SIZE, sort));
            for (T row : rows) {
                writer.row(rowMapper.apply(row));
            }
            if (rows.size() < PAGE_SIZE) {
                return;
            }
        }
    }
}