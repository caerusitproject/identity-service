package com.caerus.identity.service;

import com.caerus.identity.client.UserServiceClient;
import com.caerus.identity.dto.*;
import com.caerus.identity.entity.RefreshToken;
import com.caerus.identity.entity.UserCredentials;
import com.caerus.identity.enums.UserEventType;
import com.caerus.identity.exception.EmailAlreadyExistsException;
import com.caerus.identity.exception.InvalidCredentialsException;
import com.caerus.identity.exception.UserNotFoundException;
import com.caerus.identity.repository.UserCredentialsRepository;
import com.caerus.identity.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final PasswordResetTokenService passwordResetTokenService;
    private final ProducerTemplate producerTemplate;

    @Transactional
    public Long register(UserRegisterRequestDto request) {
        if (userCredentialsRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("User already exists with email: " + request.email());
        }

        ApiResponse<Map<String, Long>> user = userServiceClient.createUser(
                new RegisterRequest(
                        request.email(), request.firstName(), request.lastName(), request.countryCode(), request.phoneNumber(),
                        Set.of()
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

        ApiResponse<UserRolesDto> userResponse = userServiceClient.getUserByEmail(request.email());

        String accessToken = jwtUtil.generateAccessToken(userResponse.data());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(creds.getEmail());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(request.refreshToken());

        UserCredentials creds = userCredentialsRepository.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + refreshToken.getUserEmail()));


        ApiResponse<UserRolesDto> userResponse = userServiceClient.getUserByEmail(creds.getEmail());

        String accessToken = jwtUtil.generateAccessToken(userResponse.data());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        UserCredentials existingUser = getUserByEmailOrThrow(request.email());

        String resetToken = UUID.randomUUID().toString();

        passwordResetTokenService.saveToken(existingUser.getEmail(), resetToken, Duration.ofMinutes(15));

        ForgotPasswordEvent event = new ForgotPasswordEvent(existingUser.getId(), existingUser.getEmail(), resetToken,
                UserEventType.FORGOT_PASSWORD.name());

        producerTemplate.sendBody("direct:forgot-password-events", event);
        log.info("Forgot password event published for user: {}", existingUser.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = passwordResetTokenService.validateToken(request.token());

        UserCredentials existingUser = getUserByEmailOrThrow(email);

        existingUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userCredentialsRepository.save(existingUser);

        passwordResetTokenService.invalidateToken(request.token());

        log.info("Password successfully reset for user: {}", existingUser.getEmail());
    }

    private UserCredentials getUserByEmailOrThrow(String email) {
        return userCredentialsRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email id: " + email));
    }

}
