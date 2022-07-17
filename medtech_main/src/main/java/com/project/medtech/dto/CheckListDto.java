package com.project.medtech.dto;
import com.project.medtech.model.Answer;
import com.project.medtech.model.Doctor;
import com.project.medtech.model.Patient;
import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class CheckListDto {

    private Long id;
    private Patient patient;
    private Doctor doctor;
    private Time time;
    private Date date;
    private List<Answer> answers;
}
