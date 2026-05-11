package com.learn.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())  // ✅ FIX 1: CORS runs before Security blocks
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(HttpMethod.GET,    "/api/companies/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/companies/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/companies/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasRole("MANAGER")

                .requestMatchers(HttpMethod.GET,    "/api/locations/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/locations/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/locations/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/locations/**").hasRole("MANAGER")

                .requestMatchers(HttpMethod.GET,    "/api/types/**").hasAnyRole("MANAGER", "ADMIN", "USER")
                .requestMatchers(HttpMethod.POST,   "/api/types/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/types/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/types/**").hasRole("MANAGER")

                .requestMatchers(HttpMethod.GET,    "/api/assets/**").hasAnyRole("MANAGER", "ADMIN", "USER")
                .requestMatchers(HttpMethod.POST,   "/api/assets/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/assets/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/assets/**").hasRole("MANAGER")

                .requestMatchers("/api/users/**").hasRole("MANAGER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}