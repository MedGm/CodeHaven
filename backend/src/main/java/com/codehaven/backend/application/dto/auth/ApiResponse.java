package com.codehaven.backend.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    
    private Boolean success;
    private String message;
    
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }
    
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }
}
