package com.project.medtech.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.medtech.exception.FileEmptyException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.nio.file.Files;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final ContentRepository contentRepository;


    @SneakyThrows
    public String saveForContent(Long contentId, MultipartFile file) {
        ContentEntity contentEntity = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Content was not found with id: " + contentId)
                );

        contentEntity.setImageUrl(saveImage(file));

        contentRepository.save(contentEntity);

        return "Saved image for content";
    }

    @SneakyThrows
    public String saveForDoctor(Long doctorId, MultipartFile file) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with id: " + doctorId)
                );

        UserEntity userEntity = doctor.getUserEntity();

        userEntity.setImageUrl(saveImage(file));

        userRepository.save(userEntity);

        return "Saved image for doctor";
    }

    @SneakyThrows
    public String saveForPatient(MultipartFile file) {
        UserEntity userEntity = getAuthentication();

        userEntity.setImageUrl(saveImage(file));

        userRepository.save(userEntity);

        return "Saved image for patient";
    }

    @SneakyThrows
    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileEmptyException("File is empty");
        }

        final String urlKey = "cloudinary://887665211349866:__mb-CWmbXeXGbTEqDrbhA1H6NU@neobisteamfour";

        File saveFile = Files.createTempFile(
                        System.currentTimeMillis() + "",
                        Objects.requireNonNull
                                        (file.getOriginalFilename(), "File must have an extension")
                                .substring(file.getOriginalFilename().lastIndexOf("."))
                )
                .toFile();

        file.transferTo(saveFile);

        Cloudinary cloudinary = new Cloudinary((urlKey));

        Map upload = cloudinary.uploader().upload(saveFile, ObjectUtils.emptyMap());

        return (String) upload.get("url");
    }

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName());
    }

}
