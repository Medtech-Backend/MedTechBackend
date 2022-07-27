package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`insurance`")
public class Insurance {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "insurance_seq")
    @SequenceGenerator(
            name = "insurance_seq",
            sequenceName = "insurance_seq",
            allocationSize = 1)
    private Long id;

    private String territoryName;

    private String number;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "patient_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKINSURANCEPATIENT")
    )
    private Patient patient;
}
