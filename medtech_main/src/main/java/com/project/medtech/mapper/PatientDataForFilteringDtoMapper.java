package com.project.medtech.mapper;

import com.project.medtech.dto.PatientDto;
import com.project.medtech.dto.RequestPatient;
import com.project.medtech.model.Address;
import com.project.medtech.model.Patient;
import com.project.medtech.model.User;
import com.project.medtech.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientDataForFilteringDtoMapper {

    @Autowired
    public static PatientService patientService;

    public static PatientDto EntityToDto(User user) {

        Patient patient = user.getPatient();
        Address address = patient.getAddress();
        RequestPatient requestForCurrentWeek = new RequestPatient(user.getUserId());

        PatientDto dto = new PatientDto();
        dto.setFIO(user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        System.out.println("null");
        dto.setCurrentWeekOfPregnancy( patientService.getCurrentWeekOfPregnancy(requestForCurrentWeek));
        System.out.println("null");
        dto.setResidenceAddress(address.getCity() + " " + address.getVillage() + " " + address.getStreetName() + " " + address.getHouseNumber());
        dto.setStatus(user.getStatus().toString());

        return dto;
    }
}
