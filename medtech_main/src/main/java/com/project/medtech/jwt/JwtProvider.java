package com.project.medtech.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.medtech.model.UserEntity;
import io.jsonwebtoken.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.token.secret}")
    private String secret;


    public String generateAccessToken(@NonNull UserEntity userEntity) {
        Date now = new Date();
        final Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(userEntity.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(new Date(System.currentTimeMillis() + 172800000)) // 2 days
                .withClaim("roles", userEntity.getRoleEntity().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("user_id", userEntity.getUserId())
                .sign(algorithm);
    }

    public String generateRefreshToken(@NonNull UserEntity userEntity) {
        Date now = new Date();
        final Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(userEntity.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .withClaim("roles", String.valueOf((new SimpleGrantedAuthority("can:refresh"))))
                .sign(algorithm);
    }

    public boolean validateToken(@NonNull String token) {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
    }

    public Claims getClaims(@NonNull String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
