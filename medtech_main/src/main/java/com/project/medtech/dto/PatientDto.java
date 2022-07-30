package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {

    private Integer weekOfPregnancy;
    private String fullName;
    private LocalDate birthday;
    private String address;
    private String email;
    private String phoneNumber;
}
