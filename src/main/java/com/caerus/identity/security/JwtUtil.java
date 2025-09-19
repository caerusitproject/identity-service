package com.caerus.identity.security;

import com.caerus.identity.entity.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final KeyPair keyPair;
    private final long jwtExpiration;
    private final String keyId;

    public JwtUtil(RsaKeyConfig rsaKeyConfig,
                   JwtProperties properties, String keyId) throws Exception {
        this.keyPair = rsaKeyConfig.keyPair();
        this.jwtExpiration = properties.getAccessTokenExpiration();
        this.keyId = keyId;

    }

    public String generateAccessToken(UserCredentials user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(user.getRole().name()));
        claims.put("privileges", user.getRole().getPrivileges()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setHeaderParam("kid", keyId)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
