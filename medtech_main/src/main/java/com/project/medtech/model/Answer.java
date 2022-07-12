package com.project.medtech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(name = "indicators", nullable = true)
    private String indicators ;

    @Column(name = "description", nullable = true)
    private String description ;

    @JsonIgnore
    @ManyToOne(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "checklist_id", nullable = false)
    private CheckList checkList;

}

