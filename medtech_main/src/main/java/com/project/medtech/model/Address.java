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
@Table(name = "`address`")
public class Address {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "address_seq")
    @SequenceGenerator(
            name = "address_seq",
            sequenceName = "address_seq",
            allocationSize = 1)
    private Long id;
    private String city;
    private String village;
    private String streetName;
    private String houseNumber;
    private Integer apartmentNumber;
    private String phoneNumber;
    private String relativeStreetName;
    private String relativeHouseNumber;
    private Integer relativeApartmentNumber;
    private String relativePhoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "patient_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKADDRESSPATIENT")
    )
    private Patient patient;
}
