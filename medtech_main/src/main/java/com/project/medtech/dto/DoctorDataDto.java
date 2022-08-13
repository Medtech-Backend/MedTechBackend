package com.project.medtech.dto;

import lombok.Data;

@Data
public class DoctorDataDto {

    private Long doctorId;
    private String FIO;
    private String phoneNumber;
    private String email;
    private String doctorsSchedule;
    private Long countOfPatients;
    private String status;

}