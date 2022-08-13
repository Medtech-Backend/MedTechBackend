package com.project.medtech.mapper;

import com.project.medtech.dto.DrScheduleDTO;
import com.project.medtech.model.DrScheduleEntity;

public class DrScheduleMapper {
    public static DrScheduleEntity DtoToEntity(DrScheduleDTO dto) {

        DrScheduleEntity entity = new DrScheduleEntity();
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setTime_start(dto.getTime_start());
        entity.setTime_end(dto.getTime_end());
        entity.setDoctor(dto.getDoctor());

        return entity;
    }
    public static DrScheduleDTO EntityToDto(DrScheduleEntity entity) {

        DrScheduleDTO dto = new DrScheduleDTO();
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setTime_start(entity.getTime_start());
        dto.setTime_end(entity.getTime_end());
        dto.setDoctor(entity.getDoctor());

        return dto;
    }
}
