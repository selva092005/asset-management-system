package com.learn.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // FIX 4: Added common frontend ports so CORS doesn't block your frontend
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",   // Vite (React default)
            "http://localhost:3000",   // React CRA default
            "http://localhost:4200",   // Angular default
            "http://localhost:8081",   // Other local ports
            "https://*.devtunnels.ms"  // Dev tunnels
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}