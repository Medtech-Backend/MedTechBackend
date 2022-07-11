package com.project.medtech.controller;

import com.project.medtech.dto.AuthRequest;
import com.project.medtech.dto.AuthResponse;
import com.project.medtech.dto.RefreshJwtRequest;
import com.project.medtech.service.AuthService;
import com.project.medtech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken.getRefreshToken()));
    }
}
