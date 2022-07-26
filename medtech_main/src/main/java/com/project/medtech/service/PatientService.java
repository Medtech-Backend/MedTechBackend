package com.project.medtech.service;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.dto.QuestionDto;
import com.project.medtech.dto.RequestPatient;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListInfoDtoMapper;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.mapper.QuestionMapper;
import com.project.medtech.model.CheckList;
import com.project.medtech.model.Patient;
import com.project.medtech.model.Pregnancy;
import com.project.medtech.model.Question;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.PregnancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PregnancyRepository pregnancyRepository;
    private final CheckListRepository checkListRepository;

    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long ID){
        return patientRepository.findById(ID).orElseThrow(() -> new ResourceNotFoundException("No patient with ID : " + ID));

    }

    public Integer getCurrentWeekOfPregnancy(RequestPatient request) {

        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(()->new ResourceNotFoundException("No Patient with ID : "+request.getPatientId()));
        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId()).orElseThrow(()->new ResourceNotFoundException("No pregnancy with ID : "+patient.getCurrentPregnancyId()));;

        LocalDate registrationDate = pregnancy.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        Long diffInDays = ChronoUnit.DAYS.between(registrationDate, currentDate);
        Long diffInWeeks = diffInDays / 7;
        Integer currentWeek = pregnancy.getFirstVisitWeekOfPregnancy() + Integer.valueOf(diffInWeeks.intValue());
        return currentWeek;
    }

    public List<CheckListInfoDto> getAllPatientsCheckLists(RequestPatient reqPat) {
        Patient patient = patientRepository.findById(reqPat.getPatientId()).orElseThrow(()->new ResourceNotFoundException("No Patient with ID : "+reqPat.getPatientId()));
        List<CheckList> list = checkListRepository.findAllByPatient(patient);
        List<CheckListInfoDto> listDto = new ArrayList<>();

        for(CheckList checkList : list ){
            listDto.add(CheckListInfoDtoMapper.EntityToDto(checkList));
        }

        return listDto;
    }



}
