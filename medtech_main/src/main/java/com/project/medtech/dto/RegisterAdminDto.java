package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdminDto {

    private String lastName;
    private String firstName;
    private String middleName;
    private String phoneNumber;
    private String email;
    private String role;
}
