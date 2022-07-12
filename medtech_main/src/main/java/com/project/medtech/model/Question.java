package com.project.medtech.model;


import com.project.medtech.dto.enums.Status;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue
    @Column
    private Long id;
    @Column(name ="question",nullable = false)
    private String question;
    @Column(nullable = false)
    private Status status;

}
