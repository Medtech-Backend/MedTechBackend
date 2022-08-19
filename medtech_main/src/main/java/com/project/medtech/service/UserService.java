package com.project.medtech.service;

import com.project.medtech.dto.AuthResponse;
import com.project.medtech.dto.EmailDto;
import com.project.medtech.dto.EmailTextDto;
import com.project.medtech.dto.UserDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.jwt.JwtProvider;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final EmailSenderService emailSenderService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, EmailSenderService emailSenderService,
                       JwtProvider jwtProvider, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream().map(UserService::toUserModel)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return toUserModel(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found with ID: " + id)));
    }

    public UserDto getUserByEmail(EmailDto email) {
        UserEntity userEntity = userRepository.findByEmail(email.getEmail());

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + email.getEmail());
        }

        return toUserModel(userEntity);
    }

    public UserDto sendResetPassword(EmailDto email) {
        UserEntity userEntity = userRepository.findByEmail(email.getEmail());

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + email);
        }

        String resetCode = emailSenderService.send(email.getEmail(), "resetCode");

        userEntity.setResetCode(resetCode);

        userRepository.save(userEntity);

        return toUserModel(userEntity);
    }

    public EmailTextDto checkResetCode(EmailTextDto emailResetCodeDto) {
        UserEntity userEntity = userRepository.findByEmail(emailResetCodeDto.getEmail());

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + emailResetCodeDto.getEmail());
        }

        if (userEntity.getResetCode().equals(emailResetCodeDto.getText())) {
            userEntity.setResetCode("");

            userRepository.save(userEntity);

            String accessToken = jwtProvider.generateAccessToken(userEntity);

            return new EmailTextDto(emailResetCodeDto.getEmail(), accessToken);
        } else {
            throw new ResourceNotFoundException("Incorrect reset code. Try again.");
        }
    }

    public AuthResponse updatePassword(EmailTextDto emailPasswordDto) {
        UserEntity userEntity = userRepository.findByEmail(emailPasswordDto.getEmail());

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + emailPasswordDto.getEmail());
        }

        userEntity.setPassword(passwordEncoder.encode(emailPasswordDto.getText()));
        userEntity.setOtpUsed(true);

        userRepository.save(userEntity);

        String accessToken = jwtProvider.generateAccessToken(userEntity);

        String refreshToken = jwtProvider.generateRefreshToken(userEntity);

        return new AuthResponse(accessToken, refreshToken, userEntity.getUserId(),
                userEntity.getEmail(), userEntity.isOtpUsed(),
                userEntity.getRoleEntity().getName(), userEntity.getFirstName());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + username);
        }

        return UserEntity.getUserDetails(userEntity);
    }

    public static UserDto toUserModel(UserEntity userEntity) {
        UserDto userDto = new UserDto();

        userDto.setUserId(userEntity.getUserId());
        userDto.setEmail(userEntity.getEmail());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setMiddleName(userEntity.getMiddleName());
        userDto.setPhoneNumber(userEntity.getPhoneNumber());
        userDto.setOtpUsed(userEntity.isOtpUsed());
        userDto.setRole(userEntity.getRoleEntity());
        userDto.setStatus(userEntity.getStatus());

        return userDto;
    }

    public String getFullName(UserEntity userEntity) {
        String name = "";

        if (!userEntity.getLastName().isEmpty()) {
            name += userEntity.getLastName();
        }
        if (!userEntity.getFirstName().isEmpty()) {
            name += " " + userEntity.getFirstName().charAt(0) + ".";
        }
        if (!userEntity.getMiddleName().isEmpty()) {
            name += " " + userEntity.getMiddleName().charAt(0) + ".";
        }

        return name;
    }

}
