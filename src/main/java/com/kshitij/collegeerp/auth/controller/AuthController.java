package com.kshitij.collegeerp.auth.controller;

import com.kshitij.collegeerp.auth.dto.AuthResponse;
import com.kshitij.collegeerp.auth.dto.LoginRequest;
import com.kshitij.collegeerp.auth.dto.RegisterRequest;
import com.kshitij.collegeerp.auth.service.AuthService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Registered Successfully", authService.register(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login Successful", authService.login(request))
        );

    }
}
