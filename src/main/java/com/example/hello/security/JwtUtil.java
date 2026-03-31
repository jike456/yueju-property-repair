package com.example.hello.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final long expireMillis;

    public JwtUtil(@Value("${yueju.jwt.secret}") String secret,
                   @Value("${yueju.jwt.expire-seconds}") long expireSeconds) {
        // 使用 SHA-256 对任意长度 secret 做哈希，保证 key 长度满足 HMAC-SHA256 要求
        byte[] keyBytes = sha256(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expireMillis = expireSeconds * 1000;
    }

    public String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    private static byte[] sha256(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // 理论上不会发生，退化为直接使用原始字节
            return str.getBytes(StandardCharsets.UTF_8);
        }
    }
}

