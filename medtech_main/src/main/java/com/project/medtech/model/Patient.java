package com.project.medtech.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue
    @Column
    private Long id;
    @Column(name ="age",nullable = false)
    private int age;
    @Column(name ="citizenship",nullable = false)
    private String citizenship;
    @Column(name ="pin",nullable = false)
    private int PIN;;
    @Column(nullable = false)
    private String workplace;
    @Column(nullable = false)
    private String position;
    @Column(nullable = false)
    private int phoneNumber;
    @Column(nullable = false)
    private int husbandPhoneNumber;
    @Column(nullable = false)
    private String husband_workplace;
    @Column(nullable = false)
    private String husband_position;
    @Column(nullable = false)
    private String marital_status;
    @Column(nullable = false)
    private String education;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

}
