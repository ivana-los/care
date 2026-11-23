package com.example.myportfolio.config;

import com.example.myportfolio.Role;
import com.example.myportfolio.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long familyHours;
    private final long patientDays;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.family-expiration-hours}") long familyHours,
                      @Value("${jwt.patient-expiration-days}") long patientDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.familyHours = familyHours;
        this.patientDays = patientDays;
    }

    public String generate(User user) {
        Duration ttl = user.getRole() == Role.PATIENT
                ? Duration.ofDays(patientDays)
                : Duration.ofHours(familyHours);
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .claim("familyId", user.getFamily().getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}