package com.project.medtech.controller;

import com.project.medtech.model.ImageEntity;
import com.project.medtech.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Api( "REST APIs related to `Image` Entity")
public class ImageController {

    private final ImageService service;


    @ApiOperation(value = "получение фото пользователя")
    @GetMapping("/get")
    public ResponseEntity<Resource> retrieve() {
        ImageEntity imageEntity = service.getImage();
        Resource body = new ByteArrayResource(imageEntity.getData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, imageEntity.getMimeType())
                .body(body);
    }

    @ApiOperation(value = "загрузка фото пользователя")
    @PostMapping("/upload")
    public ResponseEntity<String> save(@RequestPart MultipartFile file) {
        return ResponseEntity.ok(service.save(file));
    }
}
