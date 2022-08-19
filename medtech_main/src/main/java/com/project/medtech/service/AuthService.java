package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.jwt.JwtProvider;
import com.project.medtech.model.UserEntity;
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
        UserEntity userEntity = userRepository.findByEmail(authRequest.getEmail());
        if (userEntity == null) {
            throw new AuthException("Not found user with email: " + authRequest.getEmail());
        }
        if (encoder.matches(authRequest.getPassword(), userEntity.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(userEntity);
            String refreshToken = jwtProvider.generateRefreshToken(userEntity);
            return new AuthResponse(accessToken, refreshToken, userEntity.getUserId(),
                    userEntity.getEmail(), userEntity.isOtpUsed(),
                    userEntity.getRoleEntity().getName(), userEntity.getFirstName());
        } else {
            throw new AuthException("Incorrect password for email: " + authRequest.getEmail());
        }
    }

    @SneakyThrows
    public AuthResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateToken(refreshToken)) {
            final Claims claims = jwtProvider.getClaims(refreshToken);
            final String email = claims.getSubject();
            UserEntity userEntity = userRepository.findByEmail(email);
            final String accessToken = jwtProvider.generateAccessToken(userEntity);
            final String newRefreshToken = jwtProvider.generateRefreshToken(userEntity);
            return new AuthResponse(accessToken, newRefreshToken, userEntity.getUserId(),
                    userEntity.getEmail(), userEntity.isOtpUsed(),
                    userEntity.getRoleEntity().getName(), userEntity.getFirstName());
        }
        throw new AuthException("Invalid JWT token");
    }

}
