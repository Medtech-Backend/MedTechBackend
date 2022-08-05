package com.project.medtech.dto;

import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private boolean otpUsed;
    private String resetCode;
    private RoleEntity role;
    private Status status;

}
