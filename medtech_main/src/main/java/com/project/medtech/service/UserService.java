package com.project.medtech.service;

import com.project.medtech.dto.*;

import java.util.List;

public interface UserService {

    List<UserModel> getUsers();
    UserModel getUserById(Long id);
    UserModel getUserByEmail(EmailModel email);
    UserModel registerUser(RegisterModel registerModel);
    UserModel sendResetPassword(EmailModel email);
    EmailModel checkResetCode(EmailTextModel emailResetCodeModel);
    AuthResponse updatePassword(EmailTextModel emailPasswordModel);
}
