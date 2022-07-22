package com.project.medtech.dto;

import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private boolean otpUsed;
    private String resetCode;
    private Role role;
    private Status status;

}
