package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.jwt.JwtProvider;
import com.project.medtech.model.User;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final JwtProvider jwtProvider;

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream().map(UserService::toUserModel)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return toUserModel(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found")));
    }

    public UserDto getUserByEmail(EmailDto email) {
        User user = userRepository.findByEmail(email.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + email);
        }
        return toUserModel(user);
    }

    public UserDto registerUser(RegisterDto registerDto) {
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setRole(registerDto.getRole());
        user.setStatus(Status.ACTIVE);
        user.setOtpUsed(false);
        String password = emailSenderService.send(registerDto.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));
        userRepository.save(user);
        return toUserModel(user);
    }

    public UserDto sendResetPassword(EmailDto email) {
        User user = userRepository.findByEmail(email.getEmail());
        if(user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + email);
        }
        String resetCode = emailSenderService.send(email.getEmail(), "resetCode");
        user.setResetCode(resetCode);
        userRepository.save(user);
        return toUserModel(user);
    }

    public EmailTextDto checkResetCode(EmailTextDto emailResetCodeDto) {
        User user = userRepository.findByEmail(emailResetCodeDto.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + emailResetCodeDto.getEmail());
        }
        if(user.getResetCode().equals(emailResetCodeDto.getText())) {
            user.setResetCode("");
            user = userRepository.saveAndFlush(user);;
            String accessToken = jwtProvider.generateAccessToken(user);
            return new EmailTextDto(emailResetCodeDto.getEmail(), accessToken);
        } else {
            throw new ResourceNotFoundException("Incorrect reset code. Try again.");
        }
    }

    public AuthResponse updatePassword(EmailTextDto emailPasswordDto) {
        User user = userRepository.findByEmail(emailPasswordDto.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + emailPasswordDto.getEmail());
        }
        user.setPassword(passwordEncoder().encode(emailPasswordDto.getText()));
        user.setOtpUsed(true);
        user = userRepository.saveAndFlush(user);
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, user.isOtpUsed());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + username);
        }
        return User.getUserDetails(user);
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static UserDto toUserModel(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMiddleName(user.getMiddleName());
//        userDto.setPassword(user.getPassword());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setOtpUsed(user.isOtpUsed());
        userDto.setRole(user.getRole());
        userDto.setStatus(user.getStatus());
        return userDto;
    }

    public static User toUserEntity(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setMiddleName(userDto.getMiddleName());
//        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setOtpUsed(userDto.isOtpUsed());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());
        return user;
    }

}
