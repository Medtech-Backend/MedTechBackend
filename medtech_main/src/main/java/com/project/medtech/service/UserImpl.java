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

@RequiredArgsConstructor
@Service("userImpl")
public class UserImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final JwtProvider jwtProvider;

    @Override
    public List<UserModel> getUsers() {
        return userRepository.findAll()
                .stream().map(UserImpl::toUserModel)
                .collect(Collectors.toList());
    }

    @Override
    public UserModel getUserById(Long id) {
        return toUserModel(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found")));
    }

    @Override
    public UserModel getUserByEmail(EmailModel email) {
        User user = userRepository.findByEmail(email.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + email);
        }
        return toUserModel(user);
    }

    @Override
    public UserModel registerUser(RegisterModel registerModel) {
        User user = new User();
        user.setEmail(registerModel.getEmail());
        user.setFirstName(registerModel.getFirstName());
        user.setLastName(registerModel.getLastName());
        user.setRole(registerModel.getRole());
        user.setStatus(Status.ACTIVE);
        user.setOtpUsed(false);
        String password = emailSenderService.send(registerModel.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));
        userRepository.save(user);
        return toUserModel(user);
    }

    @Override
    public UserModel sendResetPassword(EmailModel email) {
        User user = userRepository.findByEmail(email.getEmail());
        if(user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + email);
        }
        String resetCode = emailSenderService.send(email.getEmail(), "resetCode");
        user.setResetCode(resetCode);
        userRepository.save(user);
        return toUserModel(user);
    }

    @Override
    public EmailTextModel checkResetCode(EmailTextModel emailResetCodeModel) {
        User user = userRepository.findByEmail(emailResetCodeModel.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + emailResetCodeModel.getEmail());
        }
        if(user.getResetCode().equals(emailResetCodeModel.getText())) {
            user.setResetCode("");
            userRepository.save(user);
            UserModel model = toUserModel(user);
            String accessToken = jwtProvider.generateAccessToken(model);
            return new EmailTextModel(emailResetCodeModel.getEmail(), accessToken);
        } else {
            throw new ResourceNotFoundException("Incorrect reset code. Try again.");
        }
    }

    @Override
    public AuthResponse updatePassword(EmailTextModel emailPasswordModel) {
        User user = userRepository.findByEmail(emailPasswordModel.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + emailPasswordModel.getEmail());
        }
        user.setPassword(passwordEncoder().encode(emailPasswordModel.getText()));
        user.setOtpUsed(true);
        userRepository.save(user);
        UserModel model = toUserModel(user);
        String accessToken = jwtProvider.generateAccessToken(model);
        String refreshToken = jwtProvider.generateRefreshToken(model);
        return new AuthResponse(accessToken, refreshToken, model.isOtpUsed());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + username);
        }
        return UserModel.getUserDetails(user);
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static UserModel toUserModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setUserId(user.getUserId());
        userModel.setEmail(user.getEmail());
        userModel.setFirstName(user.getFirstName());
        userModel.setLastName(user.getLastName());
        userModel.setMiddleName(user.getMiddleName());
        userModel.setPassword(user.getPassword());
        userModel.setPhoneNumber(user.getPhoneNumber());
        userModel.setOtpUsed(user.isOtpUsed());
        userModel.setRole(user.getRole());
        userModel.setStatus(user.getStatus());
        return userModel;
    }

}
