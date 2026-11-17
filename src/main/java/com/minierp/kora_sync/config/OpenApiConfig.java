package com.minierp.kora_sync.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                .description("JWT obtained from the login endpoint. "
                                        + "Send it in the Authorization header as: Bearer <token>")));
    }
}