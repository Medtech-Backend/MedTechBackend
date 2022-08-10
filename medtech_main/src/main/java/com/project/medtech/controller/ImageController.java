package com.project.medtech.controller;

import com.project.medtech.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Api( "REST APIs related to `Image` Entity")
public class ImageController {

    private final ImageService service;


    @ApiOperation(value = "загрузка фото доктора (ВЕБ)")
    @PostMapping("/upload/doctor/{doctorId}")
    public ResponseEntity<String> saveDoc(@PathVariable Long doctorId, @RequestPart MultipartFile file) {
        return ResponseEntity.ok(service.saveForWeb(doctorId, file));
    }

    @ApiOperation(value = "загрузка фото пациентки (МОБ)")
    @PostMapping("/upload/patient")
    public ResponseEntity<String> savePatient(@RequestPart MultipartFile file) {
        return ResponseEntity.ok(service.saveForMob(file));
    }

}
