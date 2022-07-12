package com.project.medtech.mapper;


import com.project.medtech.dto.CheckListDto;
import com.project.medtech.model.CheckList;

public class CheckListMapper {
    public static CheckList DtoToEntity(CheckListDto dto) {

        CheckList entity = new CheckList();
        entity.setId(dto.getId());
        entity.setPatient(dto.getPatient());
        entity.setDoctor(dto.getDoctor());
        entity.setTime(dto.getTime());
        entity.setDate(dto.getDate());
        entity.setAnswers(dto.getAnswers());
        return entity;
    }
    public static CheckListDto EntityToDto(CheckList entity) {

        CheckListDto dto = new CheckListDto();
        dto.setId(entity.getId());
        dto.setPatient(entity.getPatient());
        dto.setDoctor(entity.getDoctor());
        dto.setTime(entity.getTime());
        dto.setDate(entity.getDate());
        dto.setAnswers(entity.getAnswers());
        return dto;
    }
}

