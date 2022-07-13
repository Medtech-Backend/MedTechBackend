package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final @NonNull UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UserModel>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ApiOperation(value = "получение пользователя по ID")
    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ApiOperation(value = "регистрация пользователя")
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserModel> registerUser(@RequestBody RegisterModel registerModel) {
        return ResponseEntity.ok(userService.registerUser(registerModel));
    }

    @ApiOperation(value = "отправка кода для восстановленя пароля")
    @PutMapping("/send_reset_code")
    @PreAuthorize("hasAuthority('user:forgot_password')")
    public ResponseEntity<?> sendResetPassword(@ApiParam(value = "введите почту")
                                                   @RequestBody EmailModel email) {
        return ResponseEntity.ok(userService.sendResetPassword(email));
    }

    @ApiOperation(value = "проверка кода для восстановления пароля")
    @GetMapping("/check_reset_code")
    @PreAuthorize("hasAuthority('user:forgot_password')")
    public ResponseEntity<EmailTextModel> checkResetCode(@ApiParam(value = "введите почту и код для восстановления")
                                                             @RequestBody EmailTextModel emailResetCodeModel) {
        return ResponseEntity.ok(userService.checkResetCode(emailResetCodeModel));
    }

    @ApiOperation(value = "обновление пароля при правильном вводе кода для восст-я пар.")
    @PutMapping("/update_password")
    @PreAuthorize("hasAuthority('user:update_password')")
    public ResponseEntity<AuthResponse> updatePassword(@ApiParam(value = "введите почту и новый пароль")
                                                           @RequestBody EmailTextModel emailPasswordModel) {
        return ResponseEntity.ok(userService.updatePassword(emailPasswordModel));
    }
}
