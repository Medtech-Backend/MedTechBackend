package com.project.medtech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "`checklist`")
public class CheckList {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "checklist_seq")
    @SequenceGenerator(
            name = "checklist_seq",
            sequenceName = "checklist_seq",
            allocationSize = 1)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "patient_id",
            nullable = false
    )
    private Patient patient;

    @ManyToOne
    @JoinColumn(
            name = "doctor_id",
            nullable = false
    )
    private Doctor doctor;

    @Column(nullable = false)
    private Time time;

    @Column(nullable = false)
    private Date date;

    @OneToMany(
            mappedBy="checkList",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<Answer> answers;

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAnswers(List<Answer> answers) {
        if(answers != null){
            answers.forEach(a->{
                a.setCheckList(this);
            });
        }
        this.answers = answers;
    }
}

