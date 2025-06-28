package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.auth.ApiResponse;
import com.codehaven.backend.application.dto.auth.JwtAuthenticationResponse;
import com.codehaven.backend.application.dto.auth.LoginRequest;
import com.codehaven.backend.application.dto.auth.SignUpRequest;
import com.codehaven.backend.infrastructure.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthenticationResponse response = authenticationService.signIn(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error during user authentication: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username/email or password"));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Alias for /signin to match frontend expectations
        return authenticateUser(loginRequest);
    }
    
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = authenticationService.isUsernameAvailable(username);
        return ResponseEntity.ok(
                new ApiResponse(isAvailable, 
                        isAvailable ? "Username is available" : "Username is already taken")
        );
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = authenticationService.isEmailAvailable(email);
        return ResponseEntity.ok(
                new ApiResponse(isAvailable, 
                        isAvailable ? "Email is available" : "Email is already in use")
        );
    }
}
