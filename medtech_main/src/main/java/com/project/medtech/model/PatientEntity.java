package com.project.medtech.model;

import com.project.medtech.dto.enums.Education;
import com.project.medtech.dto.enums.Married;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`patient`")
public class PatientEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "patient_seq"
    )
    @SequenceGenerator(
            name = "patient_seq",
            sequenceName = "patient_seq",
            allocationSize = 1
    )
    private Long id;

    private LocalDate birthday;

    private Integer age;

    private String pin;

    private String citizenship;

    private String patientCategory;

    private String workPlace;

    private String position;

    private String workConditions;

    private Boolean worksNow;

    private String husbandFirstName;

    private String husbandLastName;

    private String husbandMiddleName;

    private String husbandWorkPlace;

    private String husbandPosition;

    private String husbandPhoneNumber;

    private Long currentPregnancyId;

    @Enumerated(value = EnumType.STRING)
    private Married married;

    @Enumerated(value = EnumType.STRING)
    private Education education;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "patientEntity"
    )
    private AddressEntity addressEntity;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "patientEntity"
    )
    private InsuranceEntity insuranceEntity;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PregnancyEntity> pregnancy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId",
            foreignKey = @ForeignKey(name = "FKPATIENTUSER")
    )
    private UserEntity userEntity;

}
