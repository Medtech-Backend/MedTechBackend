package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.DoctorEntity;
import com.project.medtech.model.ScheduleEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.ScheduleRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DoctorRepository doctorRepository;

    private final CheckListRepository checkListRepository;


    public List<CheckListPlannedDto> getTodaysPlannedCheckLists() {
        LocalDate localDate = LocalDate.now();

        List<CheckListEntity> checkLists = checkListRepository.findAllByDateOrderByTimeAsc(localDate);

        ArrayList<CheckListPlannedDto> checkListPlanned = new ArrayList<>();

        checkLists.forEach(
                entity -> {
                    CheckListPlannedDto dto = new CheckListPlannedDto();

                    dto.setDoctorFullName(PatientService.getFullName(entity.getDoctorEntity().getUserEntity()));
                    dto.setPatientFullName(PatientService.getFullName(entity.getPatientEntity().getUserEntity()));

                    LocalDate date = entity.getDate();
                    LocalTime time = entity.getTime();

                    dto.setDate(getFormattedDate(date));
                    dto.setTime(getFormattedTime(time));

                    checkListPlanned.add(dto);
                }
        );

        return checkListPlanned;
    }

    public List<CheckListPlannedDto> getPlannedCheckLists(String doctorFullName) {
        List<Map<String, Object>> map = checkListRepository.findByDoctorsFullName(doctorFullName);

        ArrayList<CheckListPlannedDto> checkListPlanned = new ArrayList<>();

        for (Map<String, Object> m : map) {
            CheckListPlannedDto dto = new CheckListPlannedDto();

            dto.setDoctorFullName((String) m.get("doctorFullName"));
            dto.setPatientFullName((String) m.get("patientFullName"));

            LocalDate date = (LocalDate) m.get("dt");
            LocalTime time = (LocalTime) m.get("tm");

            dto.setDate(getFormattedDate(date));
            dto.setTime(getFormattedTime(time));

            checkListPlanned.add(dto);
        }

        return checkListPlanned;
    }

    public List<ScheduleDateStatusDto> getMonthSchedule(Long doctorId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        int numberOfDays = yearMonth.lengthOfMonth();

        List<ScheduleDateStatusDto> output = new ArrayList<>();

        for (int i = 1; i <= numberOfDays; i++) {
            LocalDate date = LocalDate.of(year, month, i);

            Map<String, List<LocalTime>> daySchedule = getDayScheduleForMonth(doctorId, date);

            ScheduleDateStatusDto dto = new ScheduleDateStatusDto();

            dto.setDate(date.toString());

            if (daySchedule.containsKey("free") && daySchedule.get("free").isEmpty() &&
                    daySchedule.containsKey("booked") && daySchedule.get("booked").isEmpty()) {
                dto.setStatus("GREY");
            } else if (daySchedule.containsKey("free") && daySchedule.get("free").isEmpty()) {
                dto.setStatus("GREEN");
            } else if (daySchedule.containsKey("free") && !daySchedule.get("free").isEmpty()) {
                dto.setStatus("WHITE");
            }

            output.add(dto);
        }

        return output;
    }

    public Map<String, List<LocalTime>> getDayScheduleForMonth(Long doctorId, LocalDate localDate) {
        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with id: " + doctorId)
                );

        String day = localDate.getDayOfWeek().name();

        List<ScheduleEntity> scheduleEntities = doctorEntity.getScheduleList();

        LocalTime from = null;
        LocalTime till = null;

        for (ScheduleEntity schedule : scheduleEntities) {
            if (schedule.getDayOfWeek().equals(day)) {
                from = schedule.getFrom();
                till = schedule.getTill();
                break;
            }
        }

        if (from == null || till == null) {
            return null;
        }

        int fromMinutes = from.getHour() * 60 + from.getMinute();

        int tillMinutes = till.getHour() * 60 + till.getMinute();

        int iterationTimes = (tillMinutes - fromMinutes) / 50;

        List<CheckListEntity> reservedByDateDoctor =
                checkListRepository.findByReservedByMe(doctorId, localDate);

        ArrayList<LocalTime> reservedTime = new ArrayList<>();

        ArrayList<LocalTime> freeTime = new ArrayList<>();

        for (CheckListEntity checkList : reservedByDateDoctor) {
            reservedTime.add(checkList.getTime());
        }

        for (int i = 0; i < iterationTimes; i++) {
            int compareWith = fromMinutes + (i * 50);

            LocalTime time = LocalTime.of(compareWith / 60, compareWith % 60);

            if (!reservedTime.contains(time)) {
                freeTime.add(time);
            }
        }

        Map<String, List<LocalTime>> output = new HashMap<>();

        output.put("free", freeTime);
        output.put("booked", reservedTime);

        return output;

    }

    public ScheduleDayDto getDaySchedule(Long doctorId, String date) {
        LocalDate localDate = LocalDate.parse(date);

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Doctor was not found with id: " + doctorId)
                );

        String day = localDate.getDayOfWeek().name();

        List<ScheduleEntity> scheduleEntities = doctorEntity.getScheduleList();

        LocalTime from = null;
        LocalTime till = null;

        for (ScheduleEntity schedule : scheduleEntities) {
            if (schedule.getDayOfWeek().equals(day)) {
                from = schedule.getFrom();
                till = schedule.getTill();
                break;
            }
        }

        if (from == null || till == null) {
            return null;
        }

        int fromMinutes = from.getHour() * 60 + from.getMinute();

        int tillMinutes = till.getHour() * 60 + till.getMinute();

        int iterationTimes = (tillMinutes - fromMinutes) / 50;

        List<CheckListEntity> reservedByDateDoctor =
                checkListRepository.findByReservedByMe(doctorId, localDate);

        ScheduleDayDto scheduleDayDto = new ScheduleDayDto();

        scheduleDayDto.setDoctorsFullName(PatientService.getFullName(doctorEntity.getUserEntity()));
        scheduleDayDto.setDate(localDate);

        ArrayList<BookedDto> bookedList = new ArrayList<>();

        ArrayList<LocalTime> reservedTime = new ArrayList<>();

        for (CheckListEntity checkList : reservedByDateDoctor) {
            reservedTime.add(checkList.getTime());
        }

        for (int i = 0; i < iterationTimes; i++) {
            int compareWith = fromMinutes + (i * 50);

            LocalTime time = LocalTime.of(compareWith / 60, compareWith % 60);

            BookedDto dto = new BookedDto();

            dto.setHour(time);
            dto.setBooked(reservedTime.contains(time));

            bookedList.add(dto);
        }

        scheduleDayDto.setHours(bookedList);

        return scheduleDayDto;
    }

    public CheckListProfileDto getCheckListProfile(Long doctorId, String date,
                                                   String time) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(time);

        CheckListEntity checkList = checkListRepository
                .findChecklistProfile(doctorId, localDate, localTime)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Checklist was not found with given properties.")
                );

        UserEntity doctor = checkList.getDoctorEntity().getUserEntity();
        UserEntity patient = checkList.getPatientEntity().getUserEntity();

        String doctorFullName = doctor.getLastName() + " " + doctor.getFirstName();
        doctorFullName += !doctor.getMiddleName().isEmpty() ? doctor.getMiddleName() : "";

        String patientFullName = patient.getLastName() + " " + patient.getFirstName();
        patientFullName += !patient.getMiddleName().isEmpty() ? patient.getMiddleName() : "";

        CheckListProfileDto output = new CheckListProfileDto();

        output.setDate(getFormattedDate(localDate));
        output.setTime(getFormattedTime(localTime));
        output.setDoctorName(doctorFullName);
        output.setDoctorPhoneNumber(doctor.getPhoneNumber());
        output.setPatientName(patientFullName);
        output.setPatientPhoneNumber(patient.getPhoneNumber());

        return output;
    }

    public String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        return formatDate.format(localDate);
    }

    public String getFormattedTime(LocalTime localTime) {
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
        return formatTime.format(localTime);
    }

}