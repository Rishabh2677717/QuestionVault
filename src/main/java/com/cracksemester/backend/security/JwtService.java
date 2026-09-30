package com.cracksemester.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "CrackSemesterSecretKeyForJWTAuthentication2026";

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );


    // =========================
    // GENERATE TOKEN
    // =========================

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                .signWith(secretKey)
                .compact();
    }


    // =========================
    // EXTRACT EMAIL
    // =========================

    public String extractEmail(String token) {

        return getClaims(token)
                .getSubject();
    }


    // =========================
    // GET CLAIMS
    // =========================

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // =========================
    // VALIDATE TOKEN
    // =========================

    public boolean isTokenValid(
            String token,
            String email
    ) {

        try {

            String tokenEmail =
                    extractEmail(token);

            return tokenEmail.equals(email)
                    && !isExpired(token);

        } catch (Exception e) {

            return false;
        }
    }


    private boolean isExpired(String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }
}