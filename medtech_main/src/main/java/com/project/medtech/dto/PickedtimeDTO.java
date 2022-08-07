package com.project.medtech.dto;

import com.project.medtech.dto.enums.Colors;
import lombok.*;

import java.sql.Time;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PickedtimeDTO {

    private Time time;
    private Colors colors;

}

