package com.symplifica.backend.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs
) {
    public static LoginResponse of(String token, long expiresInMs) {
        return new LoginResponse(token, "Bearer", expiresInMs);
    }
}
