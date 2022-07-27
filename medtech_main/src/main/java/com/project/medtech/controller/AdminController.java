package com.project.medtech.controller;

import com.project.medtech.dto.RegisterAdminDto;
import com.project.medtech.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Api( "REST APIs related to Admin")
public class AdminController {

    private final AdminService adminService;

    @ApiOperation(value = "регистрация нового админа")
    @PostMapping("/create")
    public ResponseEntity<RegisterAdminDto> createAdmin(@RequestBody RegisterAdminDto registerAdminDto) {
        return ResponseEntity.ok(adminService.registerAdmin(registerAdminDto));
    }
}
