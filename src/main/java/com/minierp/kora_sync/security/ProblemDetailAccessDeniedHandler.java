package com.minierp.kora_sync.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProblemDetailAccessDeniedHandler implements AccessDeniedHandler {

    private final BearerTokenAccessDeniedHandler bearerHandler = new BearerTokenAccessDeniedHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        bearerHandler.handle(request, response, accessDeniedException);
        ProblemJson.write(response, HttpStatus.FORBIDDEN, "Access denied",
                "You do not have permission to perform this action");
    }
}