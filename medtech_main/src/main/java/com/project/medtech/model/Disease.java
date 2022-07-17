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
@Table(name = "`disease`")
public class Disease {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "disease_seq")
    @SequenceGenerator(
            name = "disease_seq",
            sequenceName = "disease_seq",
            allocationSize = 1)
    private Long id;
    private String name;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "pregnancy_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKDISEASEPREGNANCY")
    )
    private Pregnancy pregnancy;
}
