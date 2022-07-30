package com.project.medtech.controller;

import com.project.medtech.dto.*;
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
@RequestMapping("/patient")
@Api( "REST APIs related to `Patient` entity")
public class PatientController {

    private final PatientService patientService;

    @ApiOperation(value = "вывод настоящей недели беременности по ID пациента")
    @GetMapping(value="/current-week-of-pregnancy")
    ResponseEntity<Integer> getCurrentWeekOfPregnancy(@RequestBody RequestPatient patient){
        return ResponseEntity.ok(patientService.getCurrentWeekOfPregnancy(patient));
    }

    @ApiOperation(value = "вывод всех чек-листов по ID пациента")
    @GetMapping(value="/patients-checklists")
    ResponseEntity<List<CheckListInfoDto>> getAllPatientsChecklists(@RequestBody RequestPatient patient){
        return ResponseEntity.ok(patientService.getAllPatientsCheckLists(patient));
    }

    @ApiOperation(value = "регистрация нового пациента")
    @PostMapping("/create")
    ResponseEntity<RegisterPatientDto> registerPatient(@RequestBody RegisterPatientDto registerPatientDto) {
        return ResponseEntity.ok(patientService.registerPatient(registerPatientDto));
    }

    @ApiOperation(value = "получение инфо о пациентке(для мобильного приложения)")
    @GetMapping("/get-info")
    ResponseEntity<PatientDto> getInfo(@RequestBody EmailDto emailDto) {
        return ResponseEntity.ok(patientService.getInfo(emailDto));
    }

    @ApiOperation(value = "изменение номера телефона пациента")
    @PutMapping("/change-phone-number")
    public ResponseEntity<PhoneNumberDto> changePhoneNumber(@RequestBody PhoneNumberDto phoneNumberDto) {
        return ResponseEntity.ok(patientService.changePhoneNumber(phoneNumberDto));
    }

    @ApiOperation(value = "изменение адреса телефона пациента")
    @PutMapping("/change-address")
    public ResponseEntity<AddressDto> changeAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(patientService.changeAddress(addressDto));
    }

    @ApiOperation(value = "изменение почты телефона пациента")
    @PutMapping("/change-email")
    public ResponseEntity<EmailDto> changeEmail(@RequestBody EmailDto emailDto) {
        return ResponseEntity.ok(patientService.changeEmail(emailDto));
    }

}
