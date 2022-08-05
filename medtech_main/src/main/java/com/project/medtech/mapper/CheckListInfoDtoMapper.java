package com.project.medtech.mapper;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.model.CheckListEntity;

public class CheckListInfoDtoMapper {

    public static CheckListInfoDto EntityToDto(CheckListEntity entity) {

        CheckListInfoDto dto = new CheckListInfoDto();
        dto.setId(entity.getId());
        dto.setAnswerEntities(entity.getAnswerEntities());
        return dto;
    }
}
