package com.caerus.identity.service;

import com.caerus.identity.client.UserServiceClient;
import com.caerus.identity.dto.ApiResponse;
import com.caerus.identity.dto.RegisterRequest;
import com.caerus.identity.exception.UserServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserIntegrationService {

    private final UserServiceClient userServiceClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "createUserFallback")
    @Retry(name = "userService")
    public ApiResponse<Map<String, Long>> createUser(RegisterRequest request) {
        return userServiceClient.createUser(request);
    }

    private ApiResponse<Map<String, Long>> createUserFallback(RegisterRequest request, Throwable ex) {
        throw new UserServiceUnavailableException("User service is unavailable. Please try again later.", ex);
    }

}
