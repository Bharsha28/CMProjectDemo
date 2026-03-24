package com.CardMaster.security.iam;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Using a stable key for development to avoid token invalidation on restart
    private final String secret = "vH9nZ7k8xP2wM4jL1qR5sT0uY3vB6nC9xZ2mX5nJ8kL4oP1qR3sT5uW8xZ0vB2n";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());
    private final long expirationMs = 3600000; // 1 hour

    // Generate token with userId, username, and role
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username) // subject = username
                .claim("userId", userId) // custom claim
                .claim("role", role)     //  custom claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    // Extract username (subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract userId
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    // Extract role
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Generic claim extractor
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
