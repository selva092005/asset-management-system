package com.learn.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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