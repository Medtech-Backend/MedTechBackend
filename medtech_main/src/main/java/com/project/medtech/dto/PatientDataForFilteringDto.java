package com.project.medtech.dto;

import lombok.Data;

@Data
public class PatientDataForFilteringDto {

    private String FIO;
    private String phoneNumber;
    private String email;
    private Integer currentWeekOfPregnancy;
    private String residenceAddress;
    private String status;

}
