package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exporter.DoctorExcelExporter;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.DoctorService;
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
@RequestMapping("/doctor")
@Api( "REST APIs related to `Doctor` Entity")
public class DoctorController {

    private final DoctorService doctorService;

    private final UserRepository userRepository;


    @ApiOperation(value = "регистрация нового доктора (ВЕБ)")
    @PostMapping("/create")
    public ResponseEntity<DoctorDto> createDoctor(@ApiParam(value = "введите данные доктора") @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.createDoctor(doctorDto));
    }

    @ApiOperation(value = "изменение доктора со стороны админа (ВЕБ)")
    @PutMapping("/update/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@ApiParam(value = "введите ID доктора") @PathVariable Long id,
                                                  @ApiParam(value = "введите обновлённые данные доктора") @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorDto));
    }

    @ApiOperation(value = "скачивание данных всех докторов в формате excel (ВЕБ)")
    @GetMapping("/excel/get-doctors")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());

        String fileName = "doctors_" + currentDateTime + ".xlsx";

        fileName = fileName.replaceAll(":", "-");

        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);
        List<UserEntity> userEntities = userRepository.findAllByRoleEntityName(Role.DOCTOR.name());

        DoctorExcelExporter excelExporter = new DoctorExcelExporter(userEntities, doctorService);

        excelExporter.export(response);

    }

    @ApiOperation(value = "вывод данных всех докторов (ВЕБ)")
    @GetMapping("/get-all")
    ResponseEntity<List<DoctorDataDto>> getAll(){
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @ApiOperation(value = "поиск данных всех докторов по ФИО (ВЕБ)")
    @GetMapping("/get-all-by-parameter/{username}")
    ResponseEntity<List<DoctorDataDto>> searchAllDoctorsByName(@ApiParam(value = "введите поисковое слово или поисковой слог") @PathVariable("username") NameRequest nameRequest){
        return ResponseEntity.ok(doctorService.searchByName(nameRequest));
    }

    @ApiOperation(value = "вывод листа состоящий из ФИО и почты доктора (ВЕБ)")
    @GetMapping("/get-full-name-email")
    public ResponseEntity<List<FullNameEmailDto>> getDoctorsFNEmail() {
        return ResponseEntity.ok(doctorService.getDoctorsFNEmail());
    }

    @ApiOperation(value = "вывод профиля доктора по айди (ВЕБ)")
    @GetMapping("/get-profile/{doctorId}")
    public ResponseEntity<DoctorProfileDto> getDoctorProfile(@ApiParam(value = "введите ID доктора") @PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorProfile(doctorId));
    }

    @ApiOperation(value = "изменение телефона доктора (ВЕБ)")
    @PutMapping("/change-phone-number")
    public ResponseEntity<PhoneNumberDto> changePhoneNumber(@ApiParam(value = "введите обновлённый номер телефона") @RequestBody PhoneNumberDto dto) {
        return ResponseEntity.ok(doctorService.changePhoneNumber(dto));
    }

}
