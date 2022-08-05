package com.project.medtech.controller;

import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exporter.DoctorExcelExporter;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.DoctorService;
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
@RequestMapping("/doctor")
@Api( "REST APIs related to `Doctor` Entity")
public class DoctorController {

    private final DoctorService doctorService;

    private final UserRepository userRepository;


    @ApiOperation(value = "регистрация нового доктора")
    @PostMapping("/create")
    public ResponseEntity<RegisterDoctorDto> createDoctor(@RequestBody RegisterDoctorDto registerDoctorDto) {
        return ResponseEntity.ok(doctorService.createDoctor(registerDoctorDto));
    }

    @ApiOperation(value = "получение чеклиста в виде excel файла")
    @GetMapping("/excel/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        currentDateTime = currentDateTime.replaceAll(":", "-");
        String headerKey = "Content-Disposition ";
        String headerValue = "attachment; filename=doctors_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<UserEntity> userEntities = userRepository.findAllByRoleEntityName(Role.DOCTOR.name());

        DoctorExcelExporter excelExporter = new DoctorExcelExporter(userEntities);
        excelExporter.export(response);

    }
}
