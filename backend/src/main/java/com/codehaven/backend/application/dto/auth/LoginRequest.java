package com.codehaven.backend.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Username or email is required")
    @JsonAlias({"username", "email"}) // Accept username, email, or usernameOrEmail
    private String usernameOrEmail;
    
    @NotBlank(message = "Password is required")
    private String password;
}
