package com.project.medtech.service;

import com.project.medtech.dto.AuthRequest;
import com.project.medtech.dto.AuthResponse;
import lombok.NonNull;

public interface AuthService {

    AuthResponse login(@NonNull AuthRequest authRequest);
    AuthResponse refresh(@NonNull String refreshToken);
}
