package com.project.medtech.service;

import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.DoctorEntity;
import com.project.medtech.model.RoleEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.RoleRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final EmailSenderService emailSenderService;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public RegisterDoctorDto createDoctor(RegisterDoctorDto registerDoctorDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerDoctorDto.getEmail());
        userEntity.setFirstName(registerDoctorDto.getFirstName());
        userEntity.setLastName(registerDoctorDto.getLastName());
        userEntity.setMiddleName(registerDoctorDto.getMiddleName());
        userEntity.setPhoneNumber(registerDoctorDto.getPhoneNumber());
        userEntity.setOtpUsed(false);
        RoleEntity roleEntity = roleRepository.findByName("DOCTOR")
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: DOCTOR")
                );
        userEntity.setRoleEntity(roleEntity);
        userEntity.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerDoctorDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setUserEntity(userEntity);

        userRepository.save(userEntity);
        doctorRepository.save(doctorEntity);

        return registerDoctorDto;
    }

}
