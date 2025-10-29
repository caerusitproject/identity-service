package com.caerus.identity.client;

import com.caerus.identity.config.FeignConfig;
import com.caerus.identity.dto.ApiResponse;
import com.caerus.identity.dto.RegisterRequest;
import com.caerus.identity.dto.UserRolesDto;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "user-service",
    url = "${services.user-service.url}",
    configuration = FeignConfig.class)
public interface UserServiceClient {

  @PostMapping("/api/v1/users")
  ApiResponse<Map<String, Long>> createUser(@RequestBody RegisterRequest request);

  @GetMapping("/api/v1/users/by-email-public")
  ApiResponse<UserRolesDto> getUserByEmail(@RequestParam String email);
}
