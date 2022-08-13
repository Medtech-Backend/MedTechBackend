package com.project.medtech.service;

import com.project.medtech.dto.RegisterAdminDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.AlreadyExistsException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.RoleEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.RoleRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EmailSenderService emailSenderService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public RegisterAdminDto registerAdmin(RegisterAdminDto registerAdminDto) {
        if (userRepository.existsByEmail(registerAdminDto.getEmail())) {
            throw new AlreadyExistsException("The given email is already used. Enter another email.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(registerAdminDto.getFirstName());
        userEntity.setLastName(registerAdminDto.getLastName());
        userEntity.setMiddleName(registerAdminDto.getMiddleName());
        userEntity.setEmail(registerAdminDto.getEmail());
        userEntity.setPhoneNumber(registerAdminDto.getPhoneNumber());
        userEntity.setOtpUsed(false);
        RoleEntity roleEntity = roleRepository.findByName(registerAdminDto.getRole())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: " + registerAdminDto.getRole())
                );
        userEntity.setRoleEntity(roleEntity);
        userEntity.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerAdminDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        userRepository.save(userEntity);

        return registerAdminDto;
    }

}
