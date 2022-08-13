package com.project.medtech.dto;


import com.project.medtech.dto.enums.Colors;
import lombok.*;

import java.sql.Date;
import java.sql.Time;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pickedtime {

    private Time time;
    private Colors colors;
    private Date date;



}

