package com.project.medtech.controller;

import com.project.medtech.dto.ContentDto;
import com.project.medtech.dto.RegisterContentDto;
import com.project.medtech.dto.UpdateContentDto;
import com.project.medtech.model.ContentEntity;
import com.project.medtech.service.ContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
@Api("REST APIs related to `Content` Entity")
public class ContentController {

    private final ContentService contentService;


    @ApiOperation(value = "получение всех контентов в упорядоченном виде")
    @GetMapping("/get-all")
    public ResponseEntity<List<ContentDto>> getContents() {
        return ResponseEntity.ok(contentService.getContents());
    }

    @ApiOperation(value = "изменение определенного контента")
    @PutMapping("/update")
    public ResponseEntity<UpdateContentDto> updateContent(@RequestBody UpdateContentDto updateContentDto) {
        return ResponseEntity.ok(contentService.updateContent(updateContentDto));
    }

    @ApiOperation(value = "создание нового контента")
    @PostMapping("/create")
    public ResponseEntity<RegisterContentDto> createContent(@RequestBody RegisterContentDto registerContentDto) {
        return ResponseEntity.ok(contentService.createContent(registerContentDto));
    }

}
