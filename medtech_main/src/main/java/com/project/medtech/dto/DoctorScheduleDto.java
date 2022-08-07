package com.project.medtech.dto;


import com.project.medtech.model.DoctorEntity;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;


@Data
public class DoctorScheduleDto {

//    private Long id;
    private Date dayOfWeek;
    private Time time_start;
    private Time time_end;
    private DoctorEntity doctor;


}
