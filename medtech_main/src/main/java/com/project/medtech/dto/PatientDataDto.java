package com.project.medtech.dto;

import lombok.Data;

@Data
public class PatientDataDto {

    private String status;
    private String residenceAddress;
    private Integer currentWeekOfPregnancy;
    private String email;
    private String phoneNumber;
    private String FIO;
    private Long patientId;

}
