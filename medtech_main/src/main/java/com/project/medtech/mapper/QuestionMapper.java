package com.project.medtech.mapper;

import com.project.medtech.dto.QuestionDto;
import com.project.medtech.model.Question;

public class QuestionMapper {

    public static Question DtoToEntity(QuestionDto dto) {

        Question entity = new Question();
        entity.setQuestion(dto.getQuestion());
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        return entity;
    }
    public static QuestionDto EntityToDto(Question entity) {

        QuestionDto dto = new QuestionDto();
        dto.setQuestion(entity.getQuestion());
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
