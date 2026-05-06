package com.learn.demo.config;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 🔐 Strong secret key (at least 256-bit)
    private final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Generate Token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract Email
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Extract Role
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ Validate Token
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    // 🔍 Common method
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}