package com.project.medtech.controller;

import com.project.medtech.dto.QuestionDto;
import com.project.medtech.service.PatientService;
import com.project.medtech.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patient")
@Api( "REST APIs related to patient entity")
public class PatientController {

    private final PatientService patientService;

    @ApiOperation(value = "вывод настоящей недели беременности")
    @GetMapping(value="/currentWeekOfPregnancy")
    ResponseEntity<Integer> getCurrentWeekOfPregnancy(Long patientID){
        return ResponseEntity.ok(patientService.getCurrentWeekOfPregnancy(patientID));
    }

}
