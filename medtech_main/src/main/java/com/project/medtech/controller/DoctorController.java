package com.project.medtech.controller;

import com.project.medtech.dto.enums.Role;
import com.project.medtech.exporter.DoctorExcelExporter;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.DoctorService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Api( "REST APIs related to `Doctor` entity")
public class DoctorController {

    private final DoctorService doctorService;
    private final UserRepository userRepository;

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition ";
        String headerValue = "attachment; filename=doctors_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<User> users = userRepository.findAll(Role.DOCTOR);

        DoctorExcelExporter excelExporter = new DoctorExcelExporter(users);
        excelExporter.export(response);

    }
}
