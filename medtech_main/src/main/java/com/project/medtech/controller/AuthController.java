package com.project.medtech.controller;

import com.project.medtech.dto.AuthRequest;
import com.project.medtech.dto.AuthResponse;
import com.project.medtech.dto.RefreshJwtRequest;
import com.project.medtech.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "авторизация пользователя")
    @PostMapping("/login")
    public ResponseEntity<?> login(@ApiParam(value = "введите почту и пароль")
                                       @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @ApiOperation(value = "обновление access и refresh токенов")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getNewRefreshToken(@ApiParam(value = "введите refresh токен")
                                                               @RequestBody RefreshJwtRequest refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken.getRefreshToken()));
    }
}
