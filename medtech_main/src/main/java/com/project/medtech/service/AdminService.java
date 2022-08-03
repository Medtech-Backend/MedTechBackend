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

    // TODO: 03.08.2022 В нескольких местах определяется данный метод
    // Нужно было вытащить как бин
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    // и в сервисах где она используется
    // private final BCryptPasswordEncoder passwordEncoder;
    // и так как у тебя сервисы анотированы @RequiredArgsConstructor
    // при создании экземпляра класса Спринг для переменной получит с бина
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
