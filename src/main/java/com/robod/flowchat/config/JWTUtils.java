package com.robod.flowchat.config;

import com.robod.flowchat.entity.UserEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtils {

    private static String secret;
    private static Key signingKey;
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 1天
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JWTUtils.secret = secret;
        JWTUtils.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public static String generateToken(String username) {
        return Jwts.builder().setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(signingKey)
                .compact();
    }

    public static String getUsernameFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
