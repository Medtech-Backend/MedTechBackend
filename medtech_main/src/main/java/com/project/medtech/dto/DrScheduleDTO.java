package com.project.medtech.dto;

import com.project.medtech.model.DoctorEntity;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
public class DrScheduleDTO {
    private Long id;
    private DayOfWeek dayOfWeek;
    private Time time_start;
    private Time time_end;
    private DoctorEntity doctor;
}
