package com.project.medtech.dto;

import com.project.medtech.model.CheckList;
import lombok.Data;

@Data
public class AnswerDto {

    private Long id;
    private String question;
    private String indicators;
    private String description;
    private CheckList checkList;

}
