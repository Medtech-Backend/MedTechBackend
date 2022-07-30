package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.AuthService;
import com.project.medtech.service.PatientService;
import com.project.medtech.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Api( "REST APIs related to Authentication")
public class ApiController {

    private final UserService userService;
    private final AuthService authService;

    @ApiOperation(value = "авторизация пользователя")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@ApiParam(value = "введите почту и пароль")
                                   @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @ApiOperation(value = "обновление access и refresh токенов")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> getNewRefreshToken(@ApiParam(value = "введите refresh токен")
                                                           @RequestBody RefreshJwtRequest refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken.getRefreshToken()));
    }

    @ApiOperation(value = "отправка кода для восстановленя пароля")
    @PutMapping("/send-reset-code")
    public ResponseEntity<UserDto> sendResetPassword(@ApiParam(value = "введите почту")
                                               @RequestBody EmailDto email) {
        return ResponseEntity.ok(userService.sendResetPassword(email));
    }

    @ApiOperation(value = "проверка кода для восстановления пароля")
    @PutMapping("/check-reset-code")
    public ResponseEntity<EmailTextDto> checkResetCode(@ApiParam(value = "введите почту и код для восстановления")
                                                       @RequestBody EmailTextDto emailResetCodeModel) {
        return ResponseEntity.ok(userService.checkResetCode(emailResetCodeModel));
    }

    @ApiOperation(value = "обновление пароля при правильном вводе кода для восст-я пар.")
    @PutMapping("/update-password")
    public ResponseEntity<AuthResponse> updatePassword(@ApiParam(value = "введите почту и новый пароль")
                                                       @RequestBody EmailTextDto emailPasswordModel) {
        return ResponseEntity.ok(userService.updatePassword(emailPasswordModel));
    }

}
