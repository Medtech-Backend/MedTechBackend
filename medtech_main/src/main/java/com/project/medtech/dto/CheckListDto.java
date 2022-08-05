package com.project.medtech.dto;
import com.project.medtech.model.AnswerEntity;
import com.project.medtech.model.DoctorEntity;
import com.project.medtech.model.PatientEntity;
import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class CheckListDto {

    private Long id;
    private PatientEntity patientEntity;
    private DoctorEntity doctorEntity;
    private Time time;
    private Date date;
    private List<AnswerEntity> answerEntities;

}
