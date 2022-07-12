package com.project.medtech.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class NewCheckListDto {

    private long patientId;
    private long doctorId;
    private Time time;
    private Date date;
}
