package com.caerus.identity.dto;

import com.caerus.identity.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank(message = "Token is required") String token,
    @NotBlank(message = "Password is required") @StrongPassword String newPassword) {}
