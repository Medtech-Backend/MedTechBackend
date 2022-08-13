package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.time.DayOfWeek;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drSchedules")
@Getter
@Setter
public class DrScheduleEntity {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(name ="day_of_week",nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name ="time_start",nullable = false)
    private Time time_start;

    @Column(name ="time_end",nullable = false)
    private Time time_end;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;

}
