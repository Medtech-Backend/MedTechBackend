package com.project.medtech.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
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
    private Date registrationDate;//
    private Integer firstVisitWeekOfPregnancy;//
    private String fromAnotherMedOrganization;
    private Integer pregnancyNumber;
    private Integer childbirthNumber;
    private Integer gestationalAgeByLastMenstruation;
    private Integer gestationalAgeByUltrasound;
    private Date estimatedDateOfBirth;
    private String lateRegistrationReason;
    private String firstVisitComplaints;
    private Double firstVisitGrowth;
    private Double firstVisitWeight;
    private Double bodyMassIndex;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "patient_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKPREGNANCYPATIENT")
    )
    private Patient patient;

    @ManyToOne
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
    private List<Disease> diseases;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "pregnancy"
    )
    private List<Appointment> appointments;

}
