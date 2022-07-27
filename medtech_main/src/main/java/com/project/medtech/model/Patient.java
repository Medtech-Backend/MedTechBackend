package com.project.medtech.model;

import com.project.medtech.dto.enums.Education;
import com.project.medtech.dto.enums.Married;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`patient`")
public class Patient {

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

    private String phoneNumber;

    private String husbandFirstName;

    private String husbandLastName;

    private String husbandMiddleName;

    private String husbandWorkPlace;

    private String husbandPosition;

    private String husbandPhoneNumber;

    private byte[] image;

    @Enumerated(value = EnumType.STRING)
    private Married married;

    @Enumerated(value = EnumType.STRING)
    private Education education;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "patient"
    )
    private Address address;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "patient"
    )
    private Insurance insurance;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "current_pregnancy_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKPATIENTPREGNANCY")
    )
    private Pregnancy pregnancy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId",
            foreignKey = @ForeignKey(name = "FKPATIENTUSER")
    )
    private User user;

}
