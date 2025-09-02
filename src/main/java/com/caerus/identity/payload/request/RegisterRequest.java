package com.caerus.identity.payload.request;

import com.caerus.identity.enums.Role;
import com.caerus.identity.validation.StrongPassword;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "firstname is required")
    private String firstname;
    @NotBlank(message = "lastname is required")
    private String lastname;
    @NotBlank(message = "email is required")
    @Email(message = "email format is not valid")
    private String email;
    @NotBlank(message = "password is required")
    @StrongPassword
    private String password;
    @NotNull
    private Role role;
}
