package com.project.medtech.dto;

import com.project.medtech.dto.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterModel {

    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
