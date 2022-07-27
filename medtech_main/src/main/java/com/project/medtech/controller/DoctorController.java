package com.project.medtech.controller;

import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/doctor")
@Api( "REST APIs related to `Doctor` Entity")
public class DoctorController {

    private final DoctorService doctorService;

    @ApiOperation("регистрация нового доктора")
    @PostMapping("/create")
    public ResponseEntity<RegisterDoctorDto> createDoctor(@RequestBody RegisterDoctorDto registerDoctorDto) {
        return ResponseEntity.ok(doctorService.createDoctor(registerDoctorDto));
    }
}
