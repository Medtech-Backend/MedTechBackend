package com.project.medtech.model;


import com.project.medtech.dto.enums.Status;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`question`")
public class QuestionEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_seq"
    )
    @SequenceGenerator(
            name = "question_seq",
            sequenceName = "question_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String question;

    @Enumerated(value = EnumType.STRING)
    private Status status;

}
