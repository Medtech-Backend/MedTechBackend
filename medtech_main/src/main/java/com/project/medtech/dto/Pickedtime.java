package com.project.medtech.dto;

import com.project.medtech.dto.enums.Colors;
import lombok.Data;

import java.sql.Time;


@Data
public class Pickedtime {

    private Time time;
    private Colors colors;

}

