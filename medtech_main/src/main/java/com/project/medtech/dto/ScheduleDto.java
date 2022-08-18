package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {

    private Long doctorId;

    private String dayOfWeek;

    private LocalTime from;

    private LocalTime till;

}
