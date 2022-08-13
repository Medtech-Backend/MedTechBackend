package com.project.medtech.dto;

import com.project.medtech.dto.enums.Colors;
import lombok.*;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservedDayDTO {
    private Time time;
    private Colors colors;


}
