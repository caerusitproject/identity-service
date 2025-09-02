package com.caerus.identity.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import com.caerus.identity.entities.RefreshToken;
import com.caerus.identity.payload.request.RefreshTokenRequest;
import com.caerus.identity.payload.response.RefreshTokenResponse;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
    ResponseCookie generateRefreshTokenCookie(String token);
    String getRefreshTokenFromCookies(HttpServletRequest request);
    void deleteByToken(String token);
    ResponseCookie getCleanRefreshTokenCookie();
}
