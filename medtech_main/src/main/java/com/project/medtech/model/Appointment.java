package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`appointment`")
public class Appointment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "appointment_seq")
    @SequenceGenerator(
            name = "appointment_seq",
            sequenceName = "appointment_seq",
            allocationSize = 1)
    private Long id;

    private String result;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "appointment_type_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKAPPOINTAPPOINTTYPE")
    )
    private AppointmentType appointmentType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "pregnancy_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKAPPOINTPREGNANCY")
    )
    private Pregnancy pregnancy;
}
