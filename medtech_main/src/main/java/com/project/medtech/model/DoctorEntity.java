package com.project.medtech.model;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`doctor`")
public class DoctorEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "doctor_seq"
    )
    @SequenceGenerator(
            name = "doctor_seq",
            sequenceName = "doctor_seq",
            allocationSize = 1
    )
    private Long id;

    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId",
            foreignKey = @ForeignKey(name = "FKDOCTORUSER")
    )
    private UserEntity userEntity;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "doctorEntity"
    )
    private List<PregnancyEntity> pregnancies;

}
