package com.project.medtech.service;

import com.project.medtech.dto.ContentDto;
import com.project.medtech.dto.RegisterContentDto;
import com.project.medtech.dto.UpdateContentDto;
import com.project.medtech.dto.enums.DefaultImageUrl;
import com.project.medtech.exception.AlreadyExistsException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.ContentEntity;
import com.project.medtech.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;


    public List<ContentDto> getContents() {
        return contentRepository.findAllByOrderByWeekNumberAscOrderAsc().stream()
                .map(this::toContentDto)
                .collect(Collectors.toList());
    }

    public UpdateContentDto updateContent(UpdateContentDto updateContentDto) {
        ContentEntity contentEntity = contentRepository.findById(updateContentDto.getContentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Content was not found with id: " + updateContentDto.getContentId())
                );

        contentEntity.setHeader(updateContentDto.getHeader());
        contentEntity.setSubtitle(updateContentDto.getSubtitle());
        contentEntity.setDescription(updateContentDto.getDescription());

        contentRepository.save(contentEntity);

        return updateContentDto;
    }

    public RegisterContentDto createContent(RegisterContentDto registerContentDto) {
        if (contentRepository.findByWeekNumberAndOrder(registerContentDto.getWeekNumber(), registerContentDto.getOrder())
                != null) {
            throw new AlreadyExistsException("Content with given week number and order is already exists.");
        }

        ContentEntity contentEntity = new ContentEntity();

        contentEntity.setHeader(registerContentDto.getHeader());
        contentEntity.setSubtitle(registerContentDto.getSubtitle());
        contentEntity.setDescription(registerContentDto.getDescription());
        contentEntity.setWeekNumber(registerContentDto.getWeekNumber());
        contentEntity.setOrder(registerContentDto.getOrder());
        contentEntity.setImageUrl(DefaultImageUrl.DEFAULT_IMAGE_ONE.getUrl());

        contentRepository.save(contentEntity);

        return registerContentDto;
    }

    private ContentDto toContentDto(ContentEntity contentEntity) {
        ContentDto contentDto = new ContentDto();

        contentDto.setContentId(contentEntity.getId());
        contentDto.setImageUrl(contentEntity.getImageUrl());
        contentDto.setHeader(contentEntity.getHeader());
        contentDto.setSubtitle(contentEntity.getSubtitle());
        contentDto.setDescription(contentEntity.getDescription());
        contentDto.setWeekNumber(contentEntity.getWeekNumber());
        contentDto.setOrder(contentEntity.getOrder());

        return contentDto;
    }

}
