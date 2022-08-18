package com.project.medtech.dto;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private Map<String, ScheduleTimeDto> schedule;

}
