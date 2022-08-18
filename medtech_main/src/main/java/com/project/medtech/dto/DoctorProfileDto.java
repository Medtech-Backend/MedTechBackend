package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileDto {

    private Long doctorId;

    private String imageUrl;

    private String firstName;

    private String lastName;

    private String middleName;

    private Long numberOfPatients;

    private String email;

    private String phoneNumber;

    private Map<String, ScheduleTimeDto> schedule;

}
