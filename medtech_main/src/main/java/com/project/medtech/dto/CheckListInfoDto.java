package com.project.medtech.dto;

import com.project.medtech.model.AnswerEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class CheckListInfoDto {

    private Long id;

    private LocalDate date;

    private List<AnswerEntity> answerEntities;

}

