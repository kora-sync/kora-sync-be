package com.minierp.kora_sync.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.media.ArraySchema;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI koraSyncOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kora Sync API")
                        .description("""
                                REST API of Kora Sync, a mini-ERP for micro and small businesses.
                                It covers companies and users, customers and suppliers, products and stock,
                                accounts payable and receivable, exportable reports and dashboard indicators.
                                """)
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("Kora Sync Team")
                                .email("support@example.com")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        JWT obtained from the login endpoint. \
                                        Send it in the Authorization header as: Bearer <token>"""))
                        .addSchemas("ProblemDetail", problemDetailSchema()));
    }

    private Schema<?> problemDetailSchema() {
        return new ObjectSchema()
                .description("Standard error body (RFC 9457 Problem Details)")
                .addProperty("type", new StringSchema()
                        .description("URI that identifies the problem type")
                        .example("about:blank"))
                .addProperty("title", new StringSchema()
                        .description("Short, human-readable summary of the problem")
                        .example("Validation failed"))
                .addProperty("status", new IntegerSchema()
                        .description("HTTP status code")
                        .example(400))
                .addProperty("detail", new StringSchema()
                        .description("Explanation specific to this occurrence of the problem")
                        .example("One or more fields are invalid"))
                .addProperty("instance", new StringSchema()
                        .description("URI of the request that caused the problem")
                        .example("/api/companies"))
                .addProperty("errors", new ArraySchema()
                        .description("Field-level validation errors. Present only on validation failures (400)")
                        .items(new ObjectSchema()
                                .addProperty("field", new StringSchema()
                                        .description("Name of the invalid field")
                                        .example("name"))
                                .addProperty("message", new StringSchema()
                                        .description("What is wrong with the field")
                                        .example("must not be blank"))));
    }
}