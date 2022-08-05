package com.project.medtech.service;

import com.project.medtech.exception.FileEmptyException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.ImageEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.ImageRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    private final UserRepository userRepository;


    public ImageEntity getImage() {
        UserEntity userEntity = getAuthentication();

        if (userEntity.getImageEntity() == null) {
            throw new ResourceNotFoundException("Image was not found for user email: " + userEntity.getEmail());
        }

        return userEntity.getImageEntity();
    }

    @SneakyThrows
    public String save(MultipartFile file) {
        UserEntity userEntity = getAuthentication();

        if (file.isEmpty()) {
            throw new FileEmptyException("File is empty");
        }

        ImageEntity imageEntity;

        if (userEntity.getImageEntity() == null) {
            imageEntity = new ImageEntity();
        } else {
            imageEntity = userEntity.getImageEntity();
        }

        imageEntity.setFilename(file.getOriginalFilename());
        imageEntity.setMimeType(file.getContentType());
        imageEntity.setData(file.getBytes());
        imageEntity.setUserEntity(userEntity);

        imageRepository.save(imageEntity);

        return "Image uploaded";
    }

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName());
    }

}
