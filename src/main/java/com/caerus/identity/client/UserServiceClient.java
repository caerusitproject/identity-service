package com.caerus.identity.client;

import com.caerus.identity.config.FeignConfig;
import com.caerus.identity.dto.ApiResponse;
import com.caerus.identity.dto.RegisterRequest;
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
    ApiResponse<Map<String, Long>> createUser(@RequestBody RegisterRequest request);

}
