package com.project.medtech.service;

import com.project.medtech.dto.DoctorDataDto;
import com.project.medtech.dto.NameRequest;
import com.project.medtech.dto.PatientDataDto;
import com.project.medtech.dto.RegisterDoctorDto;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.RoleRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String password = emailSenderService.send(registerDoctorDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setUserEntity(userEntity);

        userRepository.save(userEntity);
        doctorRepository.save(doctorEntity);

        return registerDoctorDto;
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
            dto.setCountOfPatients(getCountOfPatientsByDoctor(doctorEntity));
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }

        return listDto;
    }

    public List<DoctorDataDto> searchByName(NameRequest nameRequest) {
        List<UserEntity> userEntities = userRepository.findAllByFio(Role.DOCTOR.name(),nameRequest.getSearchWord());
        List<DoctorDataDto> listDto = new ArrayList<>();

        for (UserEntity u : userEntities) {
            DoctorDataDto dto = new DoctorDataDto();

            DoctorEntity doctorEntity = u.getDoctorEntity();
            dto.setDoctorId(doctorEntity.getId());
            dto.setFIO(userService.getFullName(u));
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setDoctorsSchedule(u.getEmail());  //изменить потом после бексултана
            dto.setCountOfPatients(getCountOfPatientsByDoctor(doctorEntity));
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }

        return listDto;
    }

    public int getCountOfPatientsByDoctor(DoctorEntity doctor){
        Set<Integer> patientsId = new HashSet<>();
        List<PregnancyEntity> pregnancies = doctor.getPregnancies();
        List<PatientEntity> allPatients = patientRepository.findAll();
        Set<PatientEntity> doctorsPatient = new HashSet<>();

            for(PregnancyEntity pregnancy : pregnancies ){
                for(PatientEntity patient : allPatients){
                    if(pregnancy == patient.getPregnancy()){
                        doctorsPatient.add(patient);
                        break;
                    } else{
                        continue;}
                }
            }
            return doctorsPatient.size();
    }






}
