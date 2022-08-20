package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.ScheduleRepository;
import com.project.medtech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final DoctorRepository doctorRepository;

    private final CheckListRepository checkListRepository;

    private final UserRepository userRepository;

    private final CheckListService checkListService;


    public Map<String, List<String>> getCurrentMonthScheduleMobile() {
        LocalDate localDate = LocalDate.now();

        List<ScheduleDateStatusDto> monthList = getMonthSchedule(
                getAuthentication()
                        .getPatientEntity()
                        .getPregnancy()
                        .getDoctorEntity()
                        .getId()
                , localDate.getYear(), localDate.getMonthValue());

        Map<String, List<String>> output = new HashMap<>();

        List<String> bookedList = new ArrayList<>();
        List<String> freeList = new ArrayList<>();

        monthList.forEach(
                m -> {
                    if (m.getStatus().equals("GREEN")) {
                        bookedList.add(m.getDate());
                    } else if (m.getStatus().equals("WHITE")) {
                        freeList.add(m.getDate());
                    }
                }
        );

        output.put("Booked", bookedList);
        output.put("Free", freeList);

        return output;
    }

    public List<DateTimeDto> getCurrentMonthPatientScheduleMobile() {
        UserEntity user = getAuthentication();

        PatientEntity patient = user.getPatientEntity();

        DoctorEntity doctor = patient.getPregnancy().getDoctorEntity();

        List<CheckListEntity> checkListEntities =
                checkListRepository.findAllByPatientEntityIdAndDoctorEntityId(patient.getId(), doctor.getId());

        List<DateTimeDto> output = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        checkListEntities.forEach(
                checkList -> {
                    DateTimeDto dto = new DateTimeDto();

                    dto.setDate(checkList.getDate().toString());
                    dto.setTime(formatter.format(checkList.getTime()));

                    output.add(dto);
                }
        );

        return output;
    }

    public List<String> getFreeHoursForDayMobile(String date) {
        UserEntity user = getAuthentication();

        LocalDate localDate = LocalDate.parse(date);

        DoctorEntity doctor = user.getPatientEntity().getPregnancy().getDoctorEntity();

        List<LocalTime> reservedHours = checkListRepository
                .findByReservedByMe(doctor.getId(), localDate)
                .stream()
                .map(CheckListEntity::getTime)
                .collect(Collectors.toList());

        ScheduleEntity schedule = scheduleRepository
                .findByDayOfWeekAndDoctorId(localDate.getDayOfWeek().name(), doctor.getId())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("Schedule was not found for day of week: "
                                        + localDate.getDayOfWeek().name())
                );

        List<String> freeHoursList = new ArrayList<>();

        int fromMinutes = schedule.getFrom().getHour() * 60 + schedule.getFrom().getMinute();

        int tillMinutes = schedule.getTill().getHour() * 60 + schedule.getTill().getMinute();

        int iterationTimes = (tillMinutes - fromMinutes) / 50;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < iterationTimes; i++) {
            int compareWith = fromMinutes + (i * 50);

            LocalTime time = LocalTime.of(compareWith / 60, compareWith % 60);

            if (!reservedHours.contains(time)) {
                freeHoursList.add(formatter.format(time));
            }
        }

        return freeHoursList;
    }

    public SimpleCheckListInfoDto registerForMeetingMobile(String date, String time) {
        UserEntity user = getAuthentication();

        PatientEntity patient = user.getPatientEntity();

        DoctorEntity doctor = patient.getPregnancy().getDoctorEntity();

        if(doctor == null) {
            throw new ResourceNotFoundException("This patient does not have a certain doctor");
        }

        SimpleCheckListInfoDto dto = new SimpleCheckListInfoDto();

        dto.setDate(LocalDate.parse(date));
        dto.setTime(LocalTime.parse(time));
        dto.setDoctorId(doctor.getId());
        dto.setPatientId(patient.getId());

        return checkListService.save(dto);
    }

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

            if(date.compareTo(LocalDate.now()) > 0) {
                if (daySchedule.containsKey("free") && daySchedule.get("free").isEmpty() &&
                        daySchedule.containsKey("booked") && !daySchedule.get("booked").isEmpty()) {
                    dto.setStatus("GREEN");
                    output.add(dto);
                } else if (daySchedule.containsKey("free") && !daySchedule.get("free").isEmpty()) {
                    dto.setStatus("WHITE");
                    output.add(dto);
                }
            }

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

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName());
    }

}