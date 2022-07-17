package com.project.medtech.dto;

import com.project.medtech.model.Pregnancy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiseaseDto {

    private Long id;
    private String name;
    private Pregnancy pregnancy;
}
