package com.project.medtech.service;

import com.project.medtech.dto.*;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();
    UserDto getUserById(Long id);
    UserDto getUserByEmail(EmailDto email);
    UserDto registerUser(RegisterDto registerDto);
    UserDto sendResetPassword(EmailDto email);
    EmailTextDto checkResetCode(EmailTextDto emailResetCodeDto);
    AuthResponse updatePassword(EmailTextDto emailPasswordDto);
}
