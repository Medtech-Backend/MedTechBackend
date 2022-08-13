package com.project.medtech.dto;

import com.project.medtech.dto.enums.Colors;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservedDateWDTO {
    private Colors colors;
    private Date date;
    private List<ReservedDayWDTO> reservedDay;
}
