package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Api( "REST APIs related to user")
public class UserController {

    private final @NonNull UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ApiOperation(value = "получение пользователя по ID")
    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ApiOperation(value = "регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    @ApiOperation(value = "отправка кода для восстановленя пароля")
    @PutMapping("/send_reset_code")
    public ResponseEntity<?> sendResetPassword(@ApiParam(value = "введите почту")
                                                   @RequestBody EmailDto email) {
        return ResponseEntity.ok(userService.sendResetPassword(email));
    }

    @ApiOperation(value = "проверка кода для восстановления пароля")
    @PutMapping("/check_reset_code")
    public ResponseEntity<EmailTextDto> checkResetCode(@ApiParam(value = "введите почту и код для восстановления")
                                                             @RequestBody EmailTextDto emailResetCodeModel) {
        return ResponseEntity.ok(userService.checkResetCode(emailResetCodeModel));
    }

    @ApiOperation(value = "обновление пароля при правильном вводе кода для восст-я пар.")
    @PutMapping("/update_password")
    public ResponseEntity<AuthResponse> updatePassword(@ApiParam(value = "введите почту и новый пароль")
                                                           @RequestBody EmailTextDto emailPasswordModel) {
        return ResponseEntity.ok(userService.updatePassword(emailPasswordModel));
    }
}
