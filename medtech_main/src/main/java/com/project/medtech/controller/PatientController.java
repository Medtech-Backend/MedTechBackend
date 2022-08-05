package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.exporter.MedCardExcelExporter;
import com.project.medtech.exporter.PatientExcelExporter;
import com.project.medtech.model.PatientEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.PregnancyRepository;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    private final PatientRepository patientRepository;

    private final PregnancyRepository pregnancyRepository;


    @ApiOperation(value = "скачивание данных всех пациентов в формате excel")
    @GetMapping("/excel/get-patients")
    public void exportPatientsToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());

        String fileName = "patients_" + currentDateTime + ".xlsx";

        fileName = fileName.replaceAll(":", "-");

        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        List<UserEntity> userEntities = userRepository.findAllByRoleEntityName(Role.PATIENT.name());

        PatientExcelExporter excelExporter = new PatientExcelExporter(userEntities, patientService);

        excelExporter.export(response);
    }

    @ApiOperation(value = "скачивание мед. карты определенного пациента")
    @GetMapping("/excel/med-card/{patientId}")
    public void exportMedCardToExcel(HttpServletResponse response, @ApiParam(value = "введите ID пациента") @PathVariable Long patientId) throws IOException {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());

        String fileName = "med-card_" + currentDateTime + ".xlsx";

        fileName = fileName.replaceAll(":", "-");

        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient was not found with ID: " + patientId));

        MedCardExcelExporter medCardExcelExporter = new MedCardExcelExporter(pregnancyRepository, patientEntity);

        medCardExcelExporter.export(response);
    }

    @ApiOperation(value = "вывод настоящей недели беременности по ID пациента")
    @GetMapping("/current-week-of-pregnancy")
    ResponseEntity<Integer> getCurrentWeekOfPregnancy(){
        return ResponseEntity.ok(patientService.getCurrentWeekOfPregnancy());
    }

    @ApiOperation(value = "вывод всех чек-листов по ID пациента")
    @GetMapping("/patients-checklists/{patientId}")
    ResponseEntity<List<CheckListInfoDto>> getAllPatientsChecklists(@PathVariable Long patientId){
        return ResponseEntity.ok(patientService.getAllPatientsCheckLists(patientId));
    }

    @ApiOperation(value = "регистрация нового пациента")
    @PostMapping("/create")
    ResponseEntity<MedCardDto> registerPatient(@RequestBody MedCardDto medCardDto) {
        return ResponseEntity.ok(patientService.registerPatient(medCardDto));
    }

    @ApiOperation(value = "получение инфо о пациентке(для мобильного приложения)")
    @GetMapping("/get-info")
    ResponseEntity<PatientDto> getInfo() {
        return ResponseEntity.ok(patientService.getInfo());
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

    @ApiOperation(value = "изменение медицинской карты пациента")
    @PutMapping("/update-med-card")
    public ResponseEntity<UpdateMedCard> updateMedCard(@RequestBody UpdateMedCard updateMedCard) {
        return ResponseEntity.ok(patientService.updateMedCard(updateMedCard));
    }

    @ApiOperation(value = "получение данных медицинской карты пациента")
    @GetMapping("/get-med-card-info")
    public ResponseEntity<MedCardDto> getPatientMedCardInfo(@RequestBody EmailDto email) {
        return ResponseEntity.ok(patientService.getPatientMedCardInfo(email));
    }

    @ApiOperation(value = "вывод данных всех пациентов")
    @GetMapping("/get-all")
    ResponseEntity<List<PatientDataDto>> getAll(){
        return ResponseEntity.ok(patientService.getAllPatients());
    }
}
