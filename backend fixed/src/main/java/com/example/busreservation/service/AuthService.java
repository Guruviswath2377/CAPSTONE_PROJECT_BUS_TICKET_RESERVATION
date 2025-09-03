package com.example.busreservation.service;

import com.example.busreservation.dto.*;
import com.example.busreservation.model.Role;
import com.example.busreservation.model.User;
import com.example.busreservation.repository.UserRepository;
import com.example.busreservation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setPassword(passwordEncoder.encode(req.getPassword()));

        Role role = (req.getRole() != null) ? req.getRole() : Role.USER;
        u.setRole(role);

        userRepository.save(u);

        AuthResponse res = new AuthResponse();
        res.setAccessToken(jwtUtil.generateToken(u.getEmail(), u.getRole().name()));
        res.setUserId(u.getId());
        res.setName(u.getName());
        res.setEmail(u.getEmail());
        res.setRole(u.getRole().name());
        return res;
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User u = userRepository.findByEmail(req.getEmail()).orElseThrow();

        AuthResponse res = new AuthResponse();
        res.setAccessToken(jwtUtil.generateToken(u.getEmail(), u.getRole().name()));
        res.setUserId(u.getId());
        res.setName(u.getName());
        res.setEmail(u.getEmail());
        res.setRole(u.getRole().name());
        return res;
    }
}
