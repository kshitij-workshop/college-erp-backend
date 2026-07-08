package com.kshitij.collegeerp.auth.controller;

import com.kshitij.collegeerp.auth.dto.AuthResponse;
import com.kshitij.collegeerp.auth.dto.LoginRequest;
import com.kshitij.collegeerp.auth.dto.RegisterRequest;
import com.kshitij.collegeerp.auth.dto.UserProfileResponse;
import com.kshitij.collegeerp.auth.service.AuthService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(
            Authentication authentication) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current logged in user",
                        authService.getCurrentUser(authentication)
                )
        );
    }
}
