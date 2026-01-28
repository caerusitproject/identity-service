package com.caerus.identity.security;

import com.caerus.identity.dto.UserRolesDto;
import com.caerus.identity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final KeyPair keyPair;
    private final long jwtExpiration;
    private final String keyId;

    public JwtUtil(RsaKeyConfig rsaKeyConfig, JwtProperties properties, String keyId)
            throws Exception {
        this.keyPair = rsaKeyConfig.keyPair();
        this.jwtExpiration = properties.getAccessTokenExpiration();
        this.keyId = keyId;
    }

    public String generateAccessToken(UserRolesDto user) {
        Map<String, Object> claims = new HashMap<>();
        Set<Role> roles =
                user.roles().stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet());

        // Extract role names for JWT
        List<String> roleNames = roles.stream().map(Enum::name).toList();

        // Extract privileges from all roles and remove duplicates
        List<String> privileges =
                roles.stream()
                        .flatMap(role -> role.getPrivileges().stream())
                        .map(Enum::name)
                        .distinct()
                        .toList();

        claims.put("roles", roleNames);
        claims.put("privileges", privileges);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.email())
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
