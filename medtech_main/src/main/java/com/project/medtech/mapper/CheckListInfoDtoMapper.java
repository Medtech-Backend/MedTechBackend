package com.project.medtech.mapper;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.model.CheckList;

public class CheckListInfoDtoMapper {

    public static CheckListInfoDto EntityToDto(CheckList entity) {

        CheckListInfoDto dto = new CheckListInfoDto();
        dto.setId(entity.getId());
        dto.setAnswers(entity.getAnswers());
        return dto;
    }
}
