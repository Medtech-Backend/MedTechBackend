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
import org.springframework.transaction.annotation.Transactional;
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


    @SneakyThrows
    @Transactional
    public String saveForWeb(Long doctorId, MultipartFile file) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with id: " + doctorId)
                );

        UserEntity userEntity = doctor.getUserEntity();

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

        userEntity.setImageUrl((String) upload.get("url"));

        userRepository.save(userEntity);

        return "Saved";
    }

    @SneakyThrows
    @Transactional
    public String saveForMob(MultipartFile file) {
        UserEntity userEntity = getAuthentication();

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

        userEntity.setImageUrl((String) upload.get("url"));

        userRepository.save(userEntity);

        return "Saved";
    }

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName());
    }

}
