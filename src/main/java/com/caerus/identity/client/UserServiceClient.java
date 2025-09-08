package com.caerus.identity.client;

import com.caerus.identity.config.FeignConfig;
import com.caerus.identity.dto.ApiResponse;
import com.caerus.identity.dto.RegisterRequest;
import com.caerus.identity.exception.UserServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient(
        name = "user-service",
        url = "${services.user-service.url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
//    @Retry(name = "userServiceRetry")
//    @CircuitBreaker(name = "userServiceCircuitBreaker") //, fallbackMethod = "fallbackCreateUser")
    ApiResponse<Map<String, Long>> createUser(@RequestBody RegisterRequest request);

//    default void fallbackCreateUser(RegisterRequest request, Throwable ex) {
//        throw new UserServiceUnavailableException("User Service unavailable, please try again later", ex);
//    }

}
