package com.project.medtech.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class NewCheckListDto {

    private long patientId;
    private long doctorId;
    private LocalTime time;
    private LocalDate date;

}
