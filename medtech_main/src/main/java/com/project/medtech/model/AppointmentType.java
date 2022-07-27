package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`appointment_type`")
public class AppointmentType {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "appointment_type_seq")
    @SequenceGenerator(
            name = "appointment_type_seq",
            sequenceName = "appointment_type_seq",
            allocationSize = 1)
    private Long id;

    private String name;
}
