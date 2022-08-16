package com.project.medtech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "`checklist`")
public class CheckListEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "checklist_seq"
    )
    @SequenceGenerator(
            name = "checklist_seq",
            sequenceName = "checklist_seq",
            allocationSize = 1
    )
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "patient_id",
            nullable = false
    )
    private PatientEntity patientEntity;

    @ManyToOne
    @JoinColumn(
            name = "doctor_id",
            nullable = false
    )
    private DoctorEntity doctorEntity;

    @Column(nullable = false)
    private Time time;

    @Column(nullable = false)
    private Date date;

    @OneToMany(
            mappedBy = "checkListEntity",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<AnswerEntity> answerEntities;

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatientEntity(PatientEntity patientEntity) {
        this.patientEntity = patientEntity;
    }

    public void setDoctorEntity(DoctorEntity doctorEntity) {
        this.doctorEntity = doctorEntity;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAnswerEntities(List<AnswerEntity> answerEntities) {
        if (answerEntities != null) {
            answerEntities.forEach(a -> {
                a.setCheckListEntity(this);
            });
        }
        this.answerEntities = answerEntities;
    }
}

