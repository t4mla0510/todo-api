package com.example.todoapi.service;

import com.example.todoapi.dto.AuthDto;
import com.example.todoapi.dto.AuthDto.TokenResponse;
import com.example.todoapi.model.RefreshToken;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.RefreshTokenRepository;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = generateRefreshToken(user);

        TokenResponse response = new TokenResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public TokenResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = token.getUser();
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = generateRefreshToken(user);

        refreshTokenRepository.deleteByUser(user);

        TokenResponse response = new TokenResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        return response;
    }

    private String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusMillis(30 * 24 * 60 * 60 * 1000))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
