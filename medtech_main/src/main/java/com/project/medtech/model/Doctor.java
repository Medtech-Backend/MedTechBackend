package com.project.medtech.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`doctor`")
public class Doctor {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "doctor_seq")
    @SequenceGenerator(
            name = "doctor_seq",
            sequenceName = "doctor_seq",
            allocationSize = 1)
    private Long id;

    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId",
            foreignKey = @ForeignKey(name = "FKDOCTORUSER")
    )
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor")
    private Set<Pregnancy> pregnancies;

}
