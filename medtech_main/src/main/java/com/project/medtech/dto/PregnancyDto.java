package com.project.medtech.dto;

import com.project.medtech.model.Appointment;
import com.project.medtech.model.Disease;
import com.project.medtech.model.Doctor;
import com.project.medtech.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PregnancyDto {

    private Long id;
    private String bloodType;
    private String rhFactorPregnant;
    private String rhFactorPartner;
    private String titerRhFactorInTwentyEightMonth;
    private String bloodRw;
    private String bloodHiv;
    private String bloodHivPartner;
    private Date registrationDate;
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

    private Patient patient;
    private Doctor doctor;
    private List<Disease> diseases;
    private List<Appointment> appointments;
}
