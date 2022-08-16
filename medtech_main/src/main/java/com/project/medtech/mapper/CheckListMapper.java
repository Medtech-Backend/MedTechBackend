package com.project.medtech.mapper;


import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.SimpleCheckListInfoDto;
import com.project.medtech.model.CheckListEntity;

public class CheckListMapper {

    public static CheckListDto EntityToDto(CheckListEntity entity) {

        CheckListDto dto = new CheckListDto();
        dto.setId(entity.getId());
        dto.setPatientEntity(entity.getPatientEntity());
        dto.setDoctorEntity(entity.getDoctorEntity());
        dto.setTime(entity.getTime());
        dto.setDate(entity.getDate());
        dto.setAnswerEntities(entity.getAnswerEntities());
        return dto;
    }

    public static SimpleCheckListInfoDto EntityToSimpleDto(CheckListEntity entity) {

        SimpleCheckListInfoDto dto = new SimpleCheckListInfoDto();
        dto.setDate(entity.getDate());
        dto.setTime(entity.getTime());
        dto.setDoctorId(entity.getDoctorEntity().getId());
        dto.setPatientId(entity.getPatientEntity().getId());
        return dto;
    }


}

