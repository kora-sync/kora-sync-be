package com.minierp.kora_sync.export;

import com.minierp.kora_sync.enums.ExportFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExportResponses {

    private final Clock clock;

    public ResponseEntity<StreamingResponseBody> download(String report, ExportFormat format,
                                                          StreamingResponseBody body) {
        String filename = "%s-%s.%s".formatted(report, LocalDate.now(clock), format.extension());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(format.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(body);
    }
}