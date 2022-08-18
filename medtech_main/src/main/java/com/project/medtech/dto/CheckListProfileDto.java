package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckListProfileDto {

    private String doctorName;

    private String doctorPhoneNumber;

    private String patientName;

    private String patientPhoneNumber;

    private String date;

    private String time;

}
