package com.nest.core.auth_service.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTUtil {
    private final RedisTemplate redisTemplate;
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret, @Qualifier("redisTemplate") RedisTemplate redisTemplate){
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.redisTemplate = redisTemplate;
    }

    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("uid", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String uid, String email, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("uid" , uid)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String uid, String email, String role, Long expiredMs){

        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expiredMs);

        String refreshToken = Jwts.builder()
                .claim("uid", uid)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                expiredMs,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }
}
