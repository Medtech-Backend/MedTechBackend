package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final @NonNull UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody RegisterModel registerModel) {
        return ResponseEntity.ok(userService.registerUser(registerModel));
    }

    @PutMapping("/sent_reset_code")
    public ResponseEntity<?> sendResetPassword(@RequestBody EmailModel email) {
        try {
            UserModel model = userService.sendResetPassword(email);
            return ResponseEntity.ok(model);
        } catch (UsernameNotFoundException e) {
            return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/check_reset_code")
    public ResponseEntity<EmailTextModel> checkResetCode(@RequestBody EmailTextModel emailResetCodeModel) {
        return ResponseEntity.ok(userService.checkResetCode(emailResetCodeModel));
    }

    @PutMapping("/update_password")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody EmailTextModel emailPasswordModel) {
        return ResponseEntity.ok(userService.updatePassword(emailPasswordModel));
    }
}
