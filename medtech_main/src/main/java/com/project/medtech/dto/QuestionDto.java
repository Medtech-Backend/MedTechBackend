package com.project.medtech.dto;

import com.project.medtech.dto.enums.Status;
import lombok.Data;

@Data
public class QuestionDto {

    private Long id;
    private String question;
    private Status status;
}

