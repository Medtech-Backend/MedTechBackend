package com.project.medtech.service;

import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.Patient;
import com.project.medtech.model.Pregnancy;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.PregnancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PregnancyRepository pregnancyRepository;

    public Integer getCurrentWeekOfPregnancy(Long id) {

        Patient patient = patientRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No Patient with ID : "+id));
        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId()).orElseThrow(()->new ResourceNotFoundException("No pregnancy with ID : "+id));;

        LocalDate registrationDate = pregnancy.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        Long diffInDays = ChronoUnit.DAYS.between(registrationDate, currentDate);
        Long diffInWeeks = diffInDays / 7;
        Integer currentWeek = pregnancy.getFirstVisitWeekOfPregnancy() + Integer.valueOf(diffInWeeks.intValue());
        return currentWeek;
    }


}
