package com.caerus.identity.service;

import com.caerus.identity.client.UserServiceClient;
import com.caerus.identity.dto.*;
import com.caerus.identity.entity.RefreshToken;
import com.caerus.identity.entity.UserCredentials;
import com.caerus.identity.exception.EmailAlreadyExistsException;
import com.caerus.identity.exception.InvalidCredentialsException;
import com.caerus.identity.exception.UserNotFoundException;
import com.caerus.identity.repository.UserCredentialsRepository;
import com.caerus.identity.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    @Transactional
    public Long register(RegisterRequest request) {
        if (userCredentialsRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("User already exists with email: " + request.email());
        }

        ApiResponse<Map<String, Long>> user = userServiceClient.createUser(
                new RegisterRequest(
                        request.email(), request.password(), request.firstName(), request.lastName(), request.countryCode(), request.phoneNumber()
                )
        );

        Long userId = user.data().get("id");

        UserCredentials creds = new UserCredentials();
        creds.setId(userId);
        creds.setEmail(request.email());
        creds.setPasswordHash(passwordEncoder.encode(request.password()));
        userCredentialsRepository.save(creds);

        return userId;
    }


    public AuthResponse login(LoginRequest request) {
        UserCredentials creds = userCredentialsRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        if (!passwordEncoder.matches(request.password(), creds.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(creds);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(creds.getEmail());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(request.refreshToken());

        UserCredentials creds = userCredentialsRepository.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + refreshToken.getUserEmail()));

        String accessToken = jwtUtil.generateAccessToken(creds);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

}
