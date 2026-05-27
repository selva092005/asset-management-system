package com.learn.demo.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    private Key key;
    private Key refreshKey;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    // Access token — 1 hour
    public String generateToken(String email, String role, String userName) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userName", userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh token — 7 days
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000L))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token, key).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token, key).get("role", String.class);
    }

    // Extract email from refresh token
    public String extractEmailFromRefreshToken(String token) {
        return getClaims(token, refreshKey).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token, key);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            getClaims(token, refreshKey);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    private Claims getClaims(String token, Key signingKey) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
