package com.example.busreservation.dto;

import com.example.busreservation.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;

    private Role role;
}
