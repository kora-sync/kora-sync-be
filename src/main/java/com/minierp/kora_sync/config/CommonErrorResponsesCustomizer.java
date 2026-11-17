package com.minierp.kora_sync.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class CommonErrorResponsesCustomizer implements OperationCustomizer {

    private static final String PROBLEM_DETAIL_REF = "#/components/schemas/ProblemDetail";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        addIfMissing(operation, "400",
                "Invalid request: malformed payload, failed validation or invalid parameter");
        addIfMissing(operation, "500", "Unexpected internal error");
        return operation;
    }

    private void addIfMissing(Operation operation, String statusCode, String description) {
        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }
        if (responses.containsKey(statusCode)) {
            return;
        }
        responses.addApiResponse(statusCode, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/problem+json",
                        new MediaType().schema(new Schema<>().$ref(PROBLEM_DETAIL_REF)))));
    }
}