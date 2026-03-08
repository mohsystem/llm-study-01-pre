package com.example.usermanagement.common;

public record ApiResponse(
        String operation,
        String status,
        String code,
        String message
) {
    public static ApiResponse accepted(String operation, String code, String message) {
        return new ApiResponse(operation, "ACCEPTED", code, message);
    }

    public static ApiResponse rejected(String operation, String code, String message) {
        return new ApiResponse(operation, "REJECTED", code, message);
    }
}
