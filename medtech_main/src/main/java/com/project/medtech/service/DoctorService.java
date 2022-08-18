package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.DefaultImageUrl;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.AlreadyExistsException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    private final ScheduleRepository scheduleRepository;


    @Transactional
    public DoctorDto createDoctor(DoctorDto doctorDto) {
        if (userRepository.existsByEmail(doctorDto.getEmail())) {
            throw new AlreadyExistsException("The given email is already used. Enter another email.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(doctorDto.getEmail());
        userEntity.setFirstName(doctorDto.getFirstName());
        userEntity.setLastName(doctorDto.getLastName());
        userEntity.setMiddleName(doctorDto.getMiddleName());
        userEntity.setPhoneNumber(doctorDto.getPhoneNumber());
        userEntity.setOtpUsed(false);
        RoleEntity roleEntity = roleRepository.findByName("DOCTOR")
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: DOCTOR")
                );
        userEntity.setRoleEntity(roleEntity);
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setImageUrl(DefaultImageUrl.DEFAULT_IMAGE_TWO.getUrl());
        String password = emailSenderService.send(doctorDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setUserEntity(userEntity);

        HashMap<String, ScheduleTimeDto> scheduleMap =
                (HashMap<String, ScheduleTimeDto>) doctorDto.getSchedule();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");

        for(DayOfWeek day: DayOfWeek.values()) {
            ScheduleEntity schedule = new ScheduleEntity();

            if(scheduleMap.containsKey(day.name())) {
                schedule.setDoctor(doctorEntity);
                schedule.setDayOfWeek(day.name());
                schedule.setFrom(LocalTime.parse(scheduleMap.get(day.name()).getFrom(), formatter));
                schedule.setTill(LocalTime.parse(scheduleMap.get(day.name()).getTill(), formatter));
            } else {
                schedule.setDoctor(doctorEntity);
                schedule.setDayOfWeek(day.name());
                schedule.setFrom(LocalTime.parse("00:00"));
                schedule.setTill(LocalTime.parse("00:00"));
            }
            scheduleRepository.save(schedule);
        }

        userRepository.save(userEntity);

        doctorRepository.save(doctorEntity);

        return doctorDto;
    }

    @Transactional
    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        DoctorEntity doctorEntity = doctorRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with ID: " + id)
                );

        UserEntity userEntity = doctorEntity.getUserEntity();
        userEntity.setEmail(doctorDto.getEmail());
        userEntity.setFirstName(doctorDto.getFirstName());
        userEntity.setLastName(doctorDto.getLastName());
        userEntity.setMiddleName(doctorDto.getMiddleName());
        userEntity.setPhoneNumber(doctorDto.getPhoneNumber());

        HashMap<String, ScheduleTimeDto> scheduleMap =
                (HashMap<String, ScheduleTimeDto>) doctorDto.getSchedule();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");

        List<ScheduleEntity> scheduleList = doctorEntity.getScheduleList();

        for(ScheduleEntity s: scheduleList) {
            if(scheduleMap.containsKey(s.getDayOfWeek())) {
                s.setFrom(LocalTime.parse(scheduleMap.get(s.getDayOfWeek()).getFrom(), formatter));
                s.setTill(LocalTime.parse(scheduleMap.get(s.getDayOfWeek()).getTill(), formatter));
            }
        }

        userRepository.save(userEntity);

        doctorRepository.save(doctorEntity);

        return doctorDto;
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
            List<ScheduleEntity> scheduleEntities = doctorEntity.getScheduleList();

            dto.setDoctorId(doctorEntity.getId());
            dto.setFIO(userService.getFullName(u));
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setCountOfPatients(getNumberOfPatients(u.getEmail()));
            dto.setDayOfWeek( String.join(" ", scheduleEntities.stream().map(sh -> sh.getDayOfWeek()).collect(Collectors.toList())));
            dto.setStatus(u.getStatus().toString());

            listDto.add(dto);
        }

        return listDto;
    }

    public List<DoctorDataDto> searchByName(NameRequest nameRequest) {
        if (!nameRequest.getSearchWord().isEmpty()) {
            if (!userRepository.findAllByFio("DOCTOR", nameRequest.getSearchWord()).isEmpty()) {
                List<UserEntity> userEntities = userRepository.findAllByFio(Role.DOCTOR.name(), nameRequest.getSearchWord());

                List<DoctorDataDto> listDto = new ArrayList<>();

                for (UserEntity u : userEntities) {
                    DoctorDataDto dto = new DoctorDataDto();

                    DoctorEntity doctorEntity = u.getDoctorEntity();
                    dto.setDoctorId(doctorEntity.getId());
                    dto.setFIO(userService.getFullName(u));
                    dto.setPhoneNumber(u.getPhoneNumber());
                    dto.setEmail(u.getEmail());
                    dto.setCountOfPatients((long) getCountOfPatientsByDoctor(doctorEntity));
                    dto.setStatus(u.getStatus().toString());

                    listDto.add(dto);

                    listDto.add(dto);
                }
                return listDto;
            } else return Collections.emptyList();
        } else return getAllDoctors();
    }

    public DoctorProfileDto getDoctorProfile(Long doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with ID: " + doctorId)
                );

        UserEntity user = doctor.getUserEntity();

        DoctorProfileDto dto = new DoctorProfileDto();

        dto.setDoctorId(doctor.getId());
        dto.setImageUrl(user.getImageUrl());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setNumberOfPatients(getNumberOfPatients(user.getEmail()));
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        Map<String, ScheduleTimeDto> scheduleTimeMap = new TreeMap<>();

        List<ScheduleEntity> schedules = doctor.getScheduleList();

        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");

        for(ScheduleEntity s: schedules) {
            ScheduleTimeDto scheduleTimeDto = new ScheduleTimeDto();

            scheduleTimeDto.setFrom(formatTime.format(s.getFrom()));
            scheduleTimeDto.setTill(formatTime.format(s.getTill()));

            scheduleTimeMap.put(s.getDayOfWeek(), scheduleTimeDto);
        }

        dto.setSchedule(scheduleTimeMap);

        return dto;
    }

    public PhoneNumberDto changePhoneNumber(PhoneNumberDto dto) {
        UserEntity user = getAuthentication();

        user.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(user);

        return dto;
    }

    public int getCountOfPatientsByDoctor(DoctorEntity doctor) {
        List<PregnancyEntity> pregnancies = doctor.getPregnancies();

        List<PatientEntity> allPatients = patientRepository.findAll();

        Set<PatientEntity> doctorsPatient = new HashSet<>();

        for (PregnancyEntity pregnancy : pregnancies) {
            for (PatientEntity patient : allPatients) {
                if (pregnancy == patient.getPregnancy()) {
                    doctorsPatient.add(patient);

                    break;
                }
            }
        }

        return doctorsPatient.size();
    }

    public Long getNumberOfPatients(String email) {
        List<PregnancyEntity> pregnancies = pregnancyRepository.findAll();

        return pregnancies.stream()
                .filter(p -> p.getDoctorEntity().getUserEntity().getEmail().equals(email))
                .count();
    }

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName());
    }

}
