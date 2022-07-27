package com.project.medtech.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDoctorDto {

    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String email;
}
