package com.project.medtech.controller;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.dto.PatientDto;
import com.project.medtech.dto.PatientFullDataDto;
import com.project.medtech.dto.RequestPatient;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exporter.PatientExcelExporter;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.PatientService;
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
    @PostMapping(value="/patients-checklists")
    ResponseEntity<List<CheckListInfoDto>> getAllPatientsChecklists(@RequestBody RequestPatient patient){
        return ResponseEntity.ok(patientService.getAllPatientsCheckLists(patient));
    }

    @ApiOperation(value = "получение пациента по ID пользователя")
    @GetMapping("/by-user-id/{userId}")
    public ResponseEntity<PatientFullDataDto> getPatientByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.getPatientDtoByUserId(userId));
    }

    @ApiOperation(value = "вывод данных всех пациентов")
    @GetMapping(value="/get-all")
    ResponseEntity<List<PatientDto>> getAll(){
        return ResponseEntity.ok(patientService.getAllPatients());
    }

}
