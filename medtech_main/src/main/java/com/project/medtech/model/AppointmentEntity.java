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
@Table(name = "`appointment`")
public class AppointmentEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "appointment_seq"
    )
    @SequenceGenerator(
            name = "appointment_seq",
            sequenceName = "appointment_seq",
            allocationSize = 1
    )
    private Long id;

    private String result;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "appointment_type_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKAPPOINTAPPOINTTYPE")
    )
    private AppointmentTypeEntity appointmentTypeEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "pregnancy_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKAPPOINTPREGNANCY")
    )
    private PregnancyEntity pregnancyEntity;
}
