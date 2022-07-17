package com.project.medtech.dto;

import com.project.medtech.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDto {

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
    private Patient patient;
}
