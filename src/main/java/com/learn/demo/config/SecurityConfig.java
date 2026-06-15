package com.learn.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

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
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ws-notifications/**").permitAll()

                        // Images served via <img src="..."> — browser never sends Auth header
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()

                        // ── Companies ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasRole("ADMIN")

                        // ── Locations ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/locations/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/locations/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/locations/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/locations/**").hasRole("ADMIN")

                        // ── Asset Types ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/types/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/types/**").hasRole("ADMIN")

                        // ── Assets ────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/assets/export").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/assets/template").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assets/dashboard").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/assets/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/assets/bulk-excel").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assets/bulk").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assets/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/assets/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/assets/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/assets/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/assets/**").hasRole("ADMIN")

                        // ── Location History ──────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/asset-history/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/asset-history/**").hasRole("ADMIN")

                        // ── Allocations ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/allocations/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/allocations/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/allocations/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Disposals ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/disposals/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/disposals/**").hasRole("ADMIN")

                        // ── Users ─────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // ── Transfers ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/transfers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/transfers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/transfers/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/transfers/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/transfers/*/receive").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/transfers/*/cancel").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/transfers/**").hasRole("ADMIN")

                        // ── Reports ───────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("ADMIN", "MANAGER", "USER")

                        // ── Notifications ─────────────────────────────────────────────
                        .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "MANAGER", "USER")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
