package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exporter.PatientExcelExporter;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.PatientService;
import com.project.medtech.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/patient")
@Api( "REST APIs related to `Patient` entity")
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;

    @ApiOperation(value = "скачивание данных всех пациентов в формате excel")
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition ";
        String headerValue = "attachment; filename=patients_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<User> users = userRepository.findAll(Role.PATIENT);

        PatientExcelExporter excelExporter = new PatientExcelExporter(users, patientService);
        excelExporter.export(response);

    }

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

    @ApiOperation(value = "вывод данных всех пациентов")
    @GetMapping(value="/get-all")
    ResponseEntity<List<PatientDataDto>> getAll(){
        return ResponseEntity.ok(patientService.getAllPatients());
    }

}
