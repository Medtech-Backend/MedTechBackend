package com.project.medtech.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientFullDataDto {

    private Long patientId;
    private Integer currentWeekOfPregnancy;
    private LocalDate birthday;
    private String city;
    private String village;
    private String streetName;
    private String houseNumber;
    private String login;
    private String phoneNumber;
}
