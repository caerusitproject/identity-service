package com.caerus.identity.service;

import com.caerus.identity.entity.RefreshToken;
import com.caerus.identity.exception.InvalidToken;
import com.caerus.identity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpirationDuration;

    @Transactional
    public RefreshToken createRefreshToken(String email){
        refreshTokenRepository.deleteByUserEmail(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserEmail(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationDuration));

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidToken("Refresh token not found L37 here"));

       if(refreshToken.getExpiryDate().isBefore(Instant.now())){
           refreshTokenRepository.delete(refreshToken);
           throw new InvalidToken("Refresh token expired");
       }

       return refreshToken;
    }

    @Transactional
    public void deleteByUserEmail(String email){
        refreshTokenRepository.deleteByUserEmail(email);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken){
        int rows = refreshTokenRepository.deleteByToken(refreshToken);
        if(rows==0){
            throw new InvalidToken("Refresh token not found Komal Singhh");
        }
    }
}
