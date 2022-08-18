package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientDtoForDoctorProfile {

    private Long patientId;

    private String fullName;

    private String phoneNumber;

    private String email;

}
