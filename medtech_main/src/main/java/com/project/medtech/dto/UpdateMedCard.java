package com.project.medtech.dto;

import com.project.medtech.dto.enums.Education;
import com.project.medtech.dto.enums.Married;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMedCard {

    private Long user_id;

    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;

    private String doctor;

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

    private Married married;
    private Education education;

    private String patientAddress;
    private String patientHomePhoneNumber;
    private String relativeAddress;
    private String relativePhoneNumber;

    private String insuranceTerritoryName;
    private String insuranceNumber;

    private String bloodType;
    private String rhFactorPregnant;
    private String rhFactorPartner;
    private String titerRhFactorInTwentyEightMonth;
    private String bloodRw;
    private String bloodHiv;
    private String bloodHivPartner;
    private LocalDate registrationDate;
    private Integer firstVisitWeekOfPregnancy;
    private String fromAnotherMedOrganizationReason;
    private String nameOfAnotherMedOrganization;
    private Integer pregnancyNumber;
    private Integer childbirthNumber;
    private Integer gestationalAgeByLastMenstruation;
    private Integer gestationalAgeByUltrasound;
    private LocalDate estimatedDateOfBirth;
    private String lateRegistrationReason;
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

    private HashMap<String, String> appointmentResults;
}
