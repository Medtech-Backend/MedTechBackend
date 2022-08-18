package com.project.medtech.mapper;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.model.AnswerEntity;

import java.util.ArrayList;
import java.util.List;

public class AnswerMapper {

    public static AnswerEntity DtoToEntity(AnswerDto dto) {

        AnswerEntity entity = new AnswerEntity();
        entity.setQuestion(dto.getQuestion());
        entity.setIndicators(dto.getIndicators());
        entity.setDescription(dto.getDescription());
        entity.setId(dto.getId());
        return entity;
    }
    public static AnswerDto EntityToDto(AnswerEntity entity) {

        AnswerDto dto = new AnswerDto();
        dto.setQuestion(entity.getQuestion());
        dto.setIndicators(entity.getIndicators());
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());

        return dto;
    }

    public static List<AnswerDto> ListEntityToListDto(List<AnswerEntity> entityList) {

        List<AnswerDto> response = new ArrayList<>();
        for(AnswerEntity entity : entityList) {
            AnswerDto dto = new AnswerDto();
            dto.setQuestion(entity.getQuestion());
            dto.setIndicators(entity.getIndicators());
            dto.setDescription(entity.getDescription());
            dto.setId(entity.getId());
            response.add(dto);
        }

        return response;
    }

}
