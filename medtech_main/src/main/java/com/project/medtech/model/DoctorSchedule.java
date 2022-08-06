package com.project.medtech.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "doctors_schedules")
@Getter
@Setter
public class DoctorSchedule {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(name ="day_of_week",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dayOfWeek;

    @Column(name ="time_start",nullable = false)
    private Time time_start;

    @Column(name ="time_end",nullable = false)
    private Time time_end;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;


}
