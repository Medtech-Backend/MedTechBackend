package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListInfoDtoMapper;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.mapper.QuestionMapper;
import com.project.medtech.model.*;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.PregnancyRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final UserRepository userRepository;
    private final UserService userService;


    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long ID){
        return patientRepository.findById(ID).orElseThrow(() -> new ResourceNotFoundException("No patient with ID : " + ID));

    }

    public PatientFullDataDto getPatientDtoByUserId(@PathVariable Long userId) {
        User user = userRepository.getById(userId);
        Patient patient = user.getPatient();
        PatientFullDataDto dto = new PatientFullDataDto();
        Address address = patient.getAddress();

        dto.setPatientId(patient.getId());
        dto.setCurrentWeekOfPregnancy(getCurrentWeekOfPregnancy(new RequestPatient(user.getUserId())));
        dto.setBirthday(patient.getBirthday());
        dto.setCity(address.getCity());
        dto.setVillage(address.getVillage());
        dto.setStreetName(address.getStreetName());
        dto.setHouseNumber(address.getHouseNumber());
        dto.setLogin(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
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

    public List<PatientDto> getAllPatients() {
        List<User> users = userRepository.findAll(Role.PATIENT);
        List<PatientDto> listDto = new ArrayList<>();

        for(User u : users ){
            PatientDto dto = new PatientDto();
            Patient patient = u.getPatient();
            Address address = patient.getAddress();
            dto.setPatientId(patient.getId());
            dto.setFIO(u.getLastName()+" "+u.getFirstName().substring(0, 1)+"."+u.getMiddleName().substring(0, 1)+".");
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setCurrentWeekOfPregnancy(getCurrentWeekOfPregnancy(new RequestPatient(u.getUserId())));
            dto.setResidenceAddress(address.getStreetName() + " " + address.getHouseNumber() + " " + address.getVillage() + " " + address.getCity());
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }
        return listDto;
    }

}
