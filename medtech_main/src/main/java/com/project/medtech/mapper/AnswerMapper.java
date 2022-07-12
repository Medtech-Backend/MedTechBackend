package com.project.medtech.mapper;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.model.Answer;

public class AnswerMapper {

    public static Answer DtoToEntity(AnswerDto dto) {

        Answer entity = new Answer();
        entity.setQuestion(dto.getQuestion());
        entity.setIndicators(dto.getIndicators());
        entity.setDescription(dto.getDescription());
        entity.setId(dto.getId());
        entity.setCheckList(dto.getCheckList());
        return entity;
    }
    public static AnswerDto EntityToDto(Answer entity) {

        AnswerDto dto = new AnswerDto();
        dto.setQuestion(entity.getQuestion());
        dto.setIndicators(entity.getIndicators());
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());
        dto.setCheckList(entity.getCheckList());

        return dto;
    }

}
