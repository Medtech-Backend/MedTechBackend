package com.project.medtech.mapper;

import com.project.medtech.dto.NewQuestionDto;
import com.project.medtech.dto.QuestionDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.QuestionEntity;

public class QuestionMapper {

    public static QuestionEntity DtoToEntity(QuestionDto dto) {

        QuestionEntity entity = new QuestionEntity();
        entity.setQuestion(dto.getQuestion());
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        return entity;
    }
    public static QuestionDto EntityToDto(QuestionEntity entity) {

        QuestionDto dto = new QuestionDto();
        dto.setQuestion(entity.getQuestion());
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public static QuestionEntity NewDtoToEntity(NewQuestionDto dto) {

        QuestionEntity entity = new QuestionEntity();
        entity.setQuestion(dto.getQuestion());
        entity.setId(dto.getId());
        entity.setStatus(Status.ACTIVE);
        return entity;
    }
}
