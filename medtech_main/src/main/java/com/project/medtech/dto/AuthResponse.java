package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private final String type = "bearer";
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private boolean otpUsed;
    private String role;
    private String firstName;

}
