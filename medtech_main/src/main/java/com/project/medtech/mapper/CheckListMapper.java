package com.project.medtech.mapper;


import com.project.medtech.dto.CheckListDto;
import com.project.medtech.model.CheckListEntity;

public class CheckListMapper {
    public static CheckListEntity DtoToEntity(CheckListDto dto) {

        CheckListEntity entity = new CheckListEntity();
        entity.setId(dto.getId());
        entity.setPatientEntity(dto.getPatientEntity());
        entity.setDoctorEntity(dto.getDoctorEntity());
        entity.setTime(dto.getTime());
        entity.setDate(dto.getDate());
        entity.setAnswerEntities(dto.getAnswerEntities());
        return entity;
    }
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
}

