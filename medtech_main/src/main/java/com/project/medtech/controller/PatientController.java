package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.exporter.MedCardExcelExporter;
import com.project.medtech.exporter.PatientExcelExporter;
import com.project.medtech.model.PatientEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.PatientRepository;
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
@Api("REST APIs related to `Patient` entity")
public class PatientController {

    private final PatientService patientService;

    private final UserRepository userRepository;

    private final PatientRepository patientRepository;


    @ApiOperation(value = "скачивание данных всех пациентов в формате excel (ВЕБ)")
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

    @ApiOperation(value = "скачивание мед. карты определенного пациента (ВЕБ)")
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
        MedCardExcelExporter medCardExcelExporter = new MedCardExcelExporter(patientEntity);

        medCardExcelExporter.export(response);
    }

    @ApiOperation(value = "вывод всех чек-листов по ID пациента (ВЕБ)")
    @GetMapping("/patients-checklists/{patientId}")
    ResponseEntity<List<CheckListInfoDto>> getAllPatientsChecklists(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getAllPatientsCheckLists(patientId));
    }

    @ApiOperation(value = "изменение номера телефона пациента (МОБ)")
    @PutMapping("/change-phone-number")
    public ResponseEntity<PhoneNumberDto> changePhoneNumber(@RequestBody PhoneNumberDto phoneNumberDto) {
        return ResponseEntity.ok(patientService.changePhoneNumber(phoneNumberDto));
    }

    @ApiOperation(value = "изменение адреса телефона пациента (МОБ)")
    @PutMapping("/change-address")
    public ResponseEntity<AddressDto> changeAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(patientService.changeAddress(addressDto));
    }

    @ApiOperation(value = "регистрация нового пациента (ВЕБ)")
    @PostMapping("/create")
    ResponseEntity<MedCardDto> registerPatient(@RequestBody MedCardDto medCardDto) {
        return ResponseEntity.ok(patientService.registerPatient(medCardDto));
    }

    @ApiOperation(value = "изменение медицинской карты пациента (ВЕБ)")
    @PutMapping("/update-med-card")
    public ResponseEntity<UpdateMedCard> updateMedCard(@RequestBody UpdateMedCard updateMedCard) {
        return ResponseEntity.ok(patientService.updateMedCard(updateMedCard));
    }

    @ApiOperation(value = "получение данных медицинской карты пациента (ВЕБ)")
    @GetMapping("/get-med-card-info/{patientId}")
    public ResponseEntity<MedCardDto> getPatientMedCardInfo(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientMedCardInfo(patientId));
    }

    @ApiOperation(value = "получение профиля пациентки (МОБ)")
    @GetMapping("/get-profile-mob")
    ResponseEntity<PatientDto> getPatientProfileMob() {
        return ResponseEntity.ok(patientService.getPatientProfileMob());
    }

    @ApiOperation(value = "вывод данных всех пациентов (ВЕБ)")
    @GetMapping("/get-all")
    public ResponseEntity<List<PatientDataDto>> getAll() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @ApiOperation(value = "получение профиля пациентки (ВЕБ)")
    @GetMapping("/get-profile-web/{patientId}")
    public ResponseEntity<PatientProfileDto> getPatientProfileWeb(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientProfileWeb(patientId));
    }

    @ApiOperation(value = "вывод типов образования (ВЕБ)")
    @GetMapping("/get-education-types")
    public ResponseEntity<List<EducationDto>> getEducationTypes() {
        return ResponseEntity.ok(patientService.getEducationTypes());
    }

    @ApiOperation(value = "вывод типов семейного положения (ВЕБ)")
    @GetMapping("/get-married-types")
    public ResponseEntity<List<MarriedDto>> getMarriedTypes() {
        return ResponseEntity.ok(patientService.getMarriedTypes());
    }

    @ApiOperation(value = "поиск данных всех пациентов по ФИО")
    @GetMapping("/get-all-by-parameter/{username}")
            ResponseEntity<List<PatientDataDto>> searchAllPatientsByName(@PathVariable("username") NameRequest nameRequest){
        return ResponseEntity.ok(patientService.searchByName(nameRequest));
    }
}
