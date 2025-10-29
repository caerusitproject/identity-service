package com.caerus.identity.service;

import com.caerus.identity.client.UserServiceClient;
import com.caerus.identity.dto.ApiResponse;
import com.caerus.identity.dto.RegisterRequest;
import com.caerus.identity.dto.UserRolesDto;
import com.caerus.identity.exception.UserServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class UserIntegrationService {

  private final UserServiceClient userServiceClient;

  @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
  @Retry(name = "userService")
  public ApiResponse<Map<String, Long>> createUser(RegisterRequest request) {
    return userServiceClient.createUser(request);
  }

  @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
  @Retry(name = "userService")
  public ApiResponse<UserRolesDto> getUserByEmail(@RequestParam String email) {
    return userServiceClient.getUserByEmail(email);
  }

  private ApiResponse<Map<String, Long>> userFallback(Throwable ex) {
    throw new UserServiceUnavailableException(
        "User service is unavailable. Please try again later.", ex);
  }
}
