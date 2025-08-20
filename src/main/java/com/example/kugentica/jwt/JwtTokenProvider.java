package com.example.kugentica.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    public final long accessTokenExpTime;
    public final long refreshTokenExpTime;
    public JwtTokenProvider(
            @Value("${spring.jwt.secret.key}") String secretKey,
            @Value("${spring.jwt.access_expiration_time}") long accessTokenExpTime,
            @Value("${spring.jwt.refresh_expiration_time}") long refreshTokenExpTime

    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }
    public String createJwt(String category,
                            String email,
                            Long expiredMs) {
        Date now = new Date();
        return Jwts.builder()
                .claim("category", category)
                .claim("email",    email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiredMs))
                .signWith(key)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException | DecodingException e) {
            log.info("🔐 Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("⌛ Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("❌ Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("⚠️ JWT claims string is empty.", e);
        }
        return false;
    }
    public Claims parseClaims(String accessToken){
            try{
                return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
            }catch (ExpiredJwtException e){
                return e.getClaims();
            }
    }

    public String getMemberEmail(String token){
            return parseClaims(token).get("email",String.class);
    }
    public String getCategory(String token){
        return parseClaims(token).get("category",String.class);
    }






}
