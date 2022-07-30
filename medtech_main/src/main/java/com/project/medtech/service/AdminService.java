package com.project.medtech.service;

import com.project.medtech.dto.RegisterAdminDto;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EmailSenderService emailSenderService;
    private final UserRepository userRepository;

    public RegisterAdminDto registerAdmin(RegisterAdminDto registerAdminDto) {
        User user = new User();
        user.setFirstName(registerAdminDto.getFirstName());
        user.setLastName(registerAdminDto.getLastName());
        user.setMiddleName(registerAdminDto.getMiddleName());
        user.setEmail(registerAdminDto.getEmail());
        user.setPhoneNumber(registerAdminDto.getPhoneNumber());
        user.setOtpUsed(false);
        user.setRole(Role.valueOf(registerAdminDto.getRole().toUpperCase()));
        user.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerAdminDto.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));

        userRepository.save(user);

        return registerAdminDto;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}