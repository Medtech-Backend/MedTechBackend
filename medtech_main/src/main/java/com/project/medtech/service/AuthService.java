package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.jwt.JwtProvider;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.message.AuthException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    private final UserRepository userRepository;


    @SneakyThrows
    public AuthResponse login(@NonNull AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail());
        if(user == null) {
            throw new AuthException("User was not found");
        }
        if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            return new AuthResponse(accessToken, refreshToken, user.isOtpUsed());
        } else {
            throw new AuthException("Incorrect password");
        }
    }

    @SneakyThrows
    public AuthResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateToken(refreshToken)) {
            final Claims claims = jwtProvider.getClaims(refreshToken);
            final String email = claims.getSubject();
            final User user = userRepository.findByEmail(email);
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new AuthResponse(accessToken, newRefreshToken, user.isOtpUsed());
        }
        throw new AuthException("Invalid JWT token");
    }

}
