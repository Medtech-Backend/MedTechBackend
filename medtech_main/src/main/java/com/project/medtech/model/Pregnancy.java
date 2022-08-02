package com.project.medtech.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`pregnancy`")
public class Pregnancy {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pregnancy_seq")
    @SequenceGenerator(
            name = "pregnancy_seq",
            sequenceName = "pregnancy_seq",
            allocationSize = 1)
    private Long id;

    private String bloodType;

    private String rhFactorPregnant;

    private String rhFactorPartner;

    private String titerRhFactorInTwentyEightMonth;

    private String bloodRw;

    private String bloodHiv;

    private String bloodHivPartner;

    private LocalDate registrationDate;

    private LocalDate firstVisitDate;

    private String fromAnotherMedOrganizationReason;

    private String nameOfAnotherMedOrganization;

    private Integer pregnancyNumber;

    private Integer childbirthNumber;

    private Integer gestationalAgeByLastMenstruation;

    private Integer gestationalAgeByUltrasound;

    private LocalDate estimatedDateOfBirth;

    private String lateRegistrationReason;

    private Integer firstVisitWeekOfPregnancy; // 2

    private String firstVisitComplaints;

    private Double firstVisitGrowth;

    private Double firstVisitWeight;

    private String bodyMassIndex;

    private String skinAndMucousMembranes;

    private String thyroid;

    private String milkGlands;

    private String peripheralLymphNodes;

    private String respiratorySystem;

    private String cardiovascularSystem;

    private String arterialPressure;

    private String digestiveSystem;

    private String urinarySystem;

    private String edema;

    private String bonePelvis;

    private String uterineFundusHeight;

    private String fetalHeartbeat;

    private String externalGenitalia;

    private String examinationOfCervixInMirrors;

    private String bimanualStudy;

    private String vaginalDischarge;

    private String provisionalDiagnosis;

    private LocalDate vacationFromForPregnancy;

    private LocalDate vacationUntilForPregnancy;

    private String allergicToDrugs;

    private String pastIllnessesAndSurgeries;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "doctor_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKPREGNANCYDOCTOR")
    )
    private Doctor doctor;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "pregnancy"
    )
    private List<Appointment> appointments;

}
