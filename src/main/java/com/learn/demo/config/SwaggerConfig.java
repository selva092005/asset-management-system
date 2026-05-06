package com.learn.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .packagesToScan("com.learn.demo.controller")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Asset Management API")
                        .version("1.0")
                        .description("API for managing assets"))

                // 🔐 ✅ ADD THIS (Security Requirement)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))

                // 🔐 ✅ ADD THIS (Security Scheme)
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))

                .servers(List.of(
                        new Server()
                                .url("https://5thq69dw-8080.inc1.devtunnels.ms/")
                                .description("Dev Tunnel Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Server")
                ));
    }
}