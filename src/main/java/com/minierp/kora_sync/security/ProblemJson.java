package com.minierp.kora_sync.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

final class ProblemJson {

    private ProblemJson() {
    }

    static void write(HttpServletResponse response, HttpStatus status, String title, String detail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("""
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s"}"""
                .formatted(title, status.value(), detail));
    }
}