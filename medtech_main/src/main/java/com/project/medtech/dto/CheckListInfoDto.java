package com.project.medtech.dto;

import com.project.medtech.model.AnswerEntity;
import lombok.Data;

import java.util.List;

@Data
public class CheckListInfoDto {

    private Long id;
    private List<AnswerEntity> answerEntities;

}

