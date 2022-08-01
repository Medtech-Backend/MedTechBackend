package com.project.medtech.service;

import com.project.medtech.exception.FileEmptyException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.Image;
import com.project.medtech.model.User;
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

    public Image getImage() {
        User user = getAuthentication();
        if(user.getImage() == null) {
            throw new ResourceNotFoundException("Image was not found for user email: " + user.getEmail());
        }
        return user.getImage();
    }

    @SneakyThrows
    public String save(MultipartFile file) {
        User user = getAuthentication();
        if (file.isEmpty()) {
            throw new FileEmptyException("File is empty");
        }
        Image image;
        if(user.getImage() == null) {
            image = new Image();
        } else {
            image = user.getImage();
        }
        image.setFilename(file.getOriginalFilename());
        image.setMimeType(file.getContentType());
        image.setData(file.getBytes());
        image.setUser(user);
        imageRepository.save(image);
        return "Image uploaded";
    }

    public User getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName());
    }
}
