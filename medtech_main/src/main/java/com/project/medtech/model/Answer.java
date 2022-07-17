package com.project.medtech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`answer`")
public class Answer {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "answer_seq")
    @SequenceGenerator(
            name = "answer_seq",
            sequenceName = "answer_seq",
            allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String question;
    private String indicators ;
    private String description ;

    @JsonIgnore
    @ManyToOne(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(
            name = "checklist_id",
            nullable = false
    )
    private CheckList checkList;

}

