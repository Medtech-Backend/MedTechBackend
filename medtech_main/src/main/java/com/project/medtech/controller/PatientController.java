package com.project.medtech.controller;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.dto.QuestionDto;
import com.project.medtech.dto.RequestPatient;
import com.project.medtech.service.PatientService;
import com.project.medtech.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("patient")
@Api( "REST APIs related to patient entity")
public class PatientController {

    private final PatientService patientService;

    @ApiOperation(value = "вывод настоящей недели беременности по ID пациента")
    @GetMapping(value="/current-week-of-pregnancy")
    ResponseEntity<Integer> getCurrentWeekOfPregnancy(@RequestBody RequestPatient patient){
        return ResponseEntity.ok(patientService.getCurrentWeekOfPregnancy(patient));
    }

    @ApiOperation(value = "вывод всех чек-листов по ID пациента")
    @PostMapping(value="/patients-checklists")
    ResponseEntity<List<CheckListInfoDto>> getAllPatientsChecklists(@RequestBody RequestPatient patient){
        return ResponseEntity.ok(patientService.getAllPatientsCheckLists(patient));
    }

}
