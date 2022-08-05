package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api("REST APIs related to `User` Entity")
public class UserController {

    private final UserService userService;


    @ApiOperation(value = "получение всех пользователей")
    @GetMapping
    public ResponseEntity<List<UserDto>> findUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ApiOperation(value = "получение пользователя по ID")
    @GetMapping("{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ApiOperation(value = "получение пользователя по почте")
    @GetMapping("/email")
    public ResponseEntity<UserDto> findUserByEmail(@RequestBody EmailDto email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

}
