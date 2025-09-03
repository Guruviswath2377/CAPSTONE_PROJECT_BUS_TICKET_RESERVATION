package com.example.busreservation.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private Long userId;
    private String name;
    private String email;
    private String role;
}
