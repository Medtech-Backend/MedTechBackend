package com.project.medtech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "checklists")
@Getter
public class CheckList {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name ="time",nullable = false)
    private Time time;

    @Column(name ="date",nullable = false)
    private Date date;

    @OneToMany(mappedBy="checkList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

