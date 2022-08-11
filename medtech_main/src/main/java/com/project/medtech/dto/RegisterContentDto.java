package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterContentDto {

    private Integer weekNumber;

    private Integer order;

    private String header;

    private String subtitle;

    private String description;

}
