package com.project.medtech.mapper;


import com.project.medtech.dto.DoctorScheduleDto;
import com.project.medtech.model.DoctorSchedule;


public class DoctorScheduleMapper {

    public static DoctorSchedule DtoToEntity(DoctorScheduleDto dto) {

        DoctorSchedule entity = new DoctorSchedule();
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setTime_start(dto.getTime_start());
        entity.setTime_end(dto.getTime_end());
        entity.setDoctor(dto.getDoctor());

        return entity;
    }
    public static DoctorScheduleDto EntityToDto(DoctorSchedule entity) {

        DoctorScheduleDto dto = new DoctorScheduleDto();
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setTime_start(entity.getTime_start());
        dto.setTime_end(entity.getTime_end());
        dto.setDoctor(entity.getDoctor());

        return dto;
    }
}
