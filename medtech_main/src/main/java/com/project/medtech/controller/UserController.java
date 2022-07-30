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
@RequiredArgsConstructor
@RequestMapping("/user")
@Api( "REST APIs related to `User` Entity")
public class UserController {

    private final @NonNull UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ApiOperation(value = "получение пользователя по ID")    //Зачем пароль?
    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ApiOperation(value = "регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

}
