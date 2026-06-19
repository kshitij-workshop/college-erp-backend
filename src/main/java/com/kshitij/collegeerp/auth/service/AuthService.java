package com.kshitij.collegeerp.auth.service;

import com.kshitij.collegeerp.auth.dto.AuthResponse;
import com.kshitij.collegeerp.auth.dto.LoginRequest;
import com.kshitij.collegeerp.auth.dto.RegisterRequest;
import com.kshitij.collegeerp.auth.entity.RefreshToken;
import com.kshitij.collegeerp.auth.entity.User;
import com.kshitij.collegeerp.auth.repository.RefreshTokenRepository;
import com.kshitij.collegeerp.auth.repository.UserRepository;
import com.kshitij.collegeerp.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiry}")
    private Long refreshTokenExpiry;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old refresh Token
        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(java.util.UUID.randomUUID().toString())
                .user(user)
                .expiryDate(java.time.Instant.now()
                        .plusMillis(refreshTokenExpiry))
                .build();
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .build();
    }
}
