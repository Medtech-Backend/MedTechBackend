package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`schedule`")
public class ScheduleEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "schedule_seq"
    )
    @SequenceGenerator(
            name = "schedule_seq",
            sequenceName = "schedule_seq",
            allocationSize = 1
    )
    private Long id;

    private String dayOfWeek;

    @Column(name = "`from`")
    private LocalTime from;

    @Column(name = "`till`")
    private LocalTime till;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "doctor_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKSCHEDULEDOCTOR")
    )
    private DoctorEntity doctor;

}
