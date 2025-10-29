package com.caerus.identity.dto;

import com.caerus.identity.validation.StrongPassword;
import jakarta.validation.constraints.*;
import java.util.Set;

public record UserRegisterRequestDto(
    @NotEmpty(message = "Email should not be empty") @Email String email,
    @NotNull(message = "Password is required") @StrongPassword String password,
    @NotBlank(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,
    @NotEmpty(message = "Last name should not be empty") String lastName,
    @Size(max = 4, message = "Country code must be 1 to 4 digits")
        @NotBlank(message = "Country code should not be empty")
        String countryCode,
    @Pattern(regexp = "\\d{4,12}", message = "Phone number must be 4 to 12 digits")
        @NotBlank(message = "Phone number should not be empty")
        String phoneNumber,
    Set<String> roles) {}
