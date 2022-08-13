package com.project.medtech.service;

import com.project.medtech.dto.DoctorDataDto;
import com.project.medtech.dto.NameRequest;
import com.project.medtech.dto.PatientDataDto;
import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;
import com.project.medtech.dto.FullNameEmailDto;
import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.DefaultImageUrl;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.DoctorEntity;
import com.project.medtech.model.RoleEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.PregnancyRepository;
import com.project.medtech.repository.RoleRepository;
import com.project.medtech.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final EmailSenderService emailSenderService;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserService userService;

    private final PatientRepository patientRepository;

    private final PregnancyRepository pregnancyRepository;


    public RegisterDoctorDto createDoctor(RegisterDoctorDto registerDoctorDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerDoctorDto.getEmail());
        userEntity.setFirstName(registerDoctorDto.getFirstName());
        userEntity.setLastName(registerDoctorDto.getLastName());
        userEntity.setMiddleName(registerDoctorDto.getMiddleName());
        userEntity.setPhoneNumber(registerDoctorDto.getPhoneNumber());
        userEntity.setOtpUsed(false);
        RoleEntity roleEntity = roleRepository.findByName("DOCTOR")
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: DOCTOR")
                );
        userEntity.setRoleEntity(roleEntity);
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setImageUrl(DefaultImageUrl.DEFAULT_IMAGE_TWO.getUrl());
        String password = emailSenderService.send(registerDoctorDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setUserEntity(userEntity);

        userRepository.save(userEntity);
        doctorRepository.save(doctorEntity);

        return registerDoctorDto;
    }

    public List<FullNameEmailDto> getDoctorsFNEmail() {
        List<DoctorEntity> doctors = doctorRepository.findAll();

        List<FullNameEmailDto> result = new ArrayList<>();

        doctors.forEach(d -> {
            UserEntity user = d.getUserEntity();

            String fullName = user.getLastName() + " " + user.getFirstName();
            fullName += !user.getMiddleName().isEmpty() ? user.getMiddleName() : "";

            result.add(new FullNameEmailDto(fullName, user.getEmail()));
        });

        return result;
    }

    public List<DoctorDataDto> getAllDoctors() {
        List<UserEntity> userEntities = userRepository.findAllByRoleEntityName(Role.DOCTOR.name());
        List<DoctorDataDto> listDto = new ArrayList<>();

        for (UserEntity u : userEntities) {
            DoctorDataDto dto = new DoctorDataDto();

            DoctorEntity doctorEntity = u.getDoctorEntity();
            dto.setDoctorId(doctorEntity.getId());
            dto.setFIO(userService.getFullName(u));
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setDoctorsSchedule(u.getEmail());  //изменить потом после бексултана
            dto.setCountOfPatients(getNumberOfPatients(u.getEmail()));
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }

        return listDto;
    }

    public List<DoctorDataDto> searchByName(NameRequest nameRequest) {
        if(nameRequest.getSearchWord().isEmpty() == false){
            if(userRepository.findAllByFio("DOCTOR", nameRequest.getSearchWord()).isEmpty() == false) {
                List<UserEntity> userEntities = userRepository.findAllByFio(Role.DOCTOR.name(), nameRequest.getSearchWord());
                List<DoctorDataDto> listDto = new ArrayList<>();

                for (UserEntity u : userEntities) {
                    DoctorDataDto dto = new DoctorDataDto();
                    DoctorEntity doctorEntity = u.getDoctorEntity();
                    dto.setDoctorId(doctorEntity.getId());
                    dto.setFIO(userService.getFullName(u));
                    dto.setPhoneNumber(u.getPhoneNumber());
                    dto.setEmail(u.getEmail());
                    dto.setDoctorsSchedule(u.getEmail());  //изменить потом после бексултана
                    dto.setCountOfPatients((long) getCountOfPatientsByDoctor(doctorEntity));
                    dto.setStatus(u.getStatus().toString());
                    listDto.add(dto);
                }
                return listDto;
            }else return Collections.<DoctorDataDto>emptyList();
        }else return getAllDoctors();
    }

    public int getCountOfPatientsByDoctor(DoctorEntity doctor){
        List<PregnancyEntity> pregnancies = doctor.getPregnancies();
        List<PatientEntity> allPatients = patientRepository.findAll();
        Set<PatientEntity> doctorsPatient = new HashSet<>();

            for(PregnancyEntity pregnancy : pregnancies ){
                for(PatientEntity patient : allPatients){
                    if(pregnancy == patient.getPregnancy()){
                        doctorsPatient.add(patient);
                        break;
                    } else{
                        continue;
                    }
                }
            }
            return doctorsPatient.size();
    }

    public Long getNumberOfPatients(String email) {
        List<PregnancyEntity> pregnancies = pregnancyRepository.findAll();

        Long count =
                pregnancies.stream()
                        .filter(p -> p.getDoctorEntity().getUserEntity().getEmail().equals(email))
                        .count();

        return count;
    }

}
