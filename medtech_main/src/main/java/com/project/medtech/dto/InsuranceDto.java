package com.project.medtech.dto;

import com.project.medtech.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InsuranceDto {

    private Long id;
    private String territoryName;
    private String number;
    private Patient patient;
}
