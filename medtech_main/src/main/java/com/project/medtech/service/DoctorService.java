package com.project.medtech.service;

import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.Doctor;
import com.project.medtech.model.User;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final EmailSenderService emailSenderService;

    public RegisterDoctorDto createDoctor(RegisterDoctorDto registerDoctorDto) {
        User user = new User();
        user.setEmail(registerDoctorDto.getEmail());
        user.setFirstName(registerDoctorDto.getFirstName());
        user.setLastName(registerDoctorDto.getLastName());
        user.setMiddleName(registerDoctorDto.getMiddleName());
        user.setPhoneNumber(registerDoctorDto.getPhoneNumber());
        user.setOtpUsed(false);
        user.setRole(Role.DOCTOR);
        user.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerDoctorDto.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));

        Doctor doctor = new Doctor();
        doctor.setUser(user);

        userRepository.save(user);
        doctorRepository.save(doctor);

        return registerDoctorDto;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
