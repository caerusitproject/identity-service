package com.caerus.identity.dto;

import com.caerus.identity.validation.StrongPassword;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotEmpty(message = "Email should not be empty")
        @Email
        String email,

        @NotNull(message = "Password is required")
        @StrongPassword
        String password,

        @NotBlank(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @NotEmpty(message = "Last name should not be empty")
        String lastName,

        @NotEmpty(message = "Phone number should not be empty")
        @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 numbers")
        String phone
) {}
