package com.cracksemester.backend.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtConfig {

    public static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(
                    "CrackSemesterSuperSecretKey2026ForJWTAuthentication123456"
                            .getBytes()
            );

    public static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 Hours

}