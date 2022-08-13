package com.project.medtech.service;


import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Colors;
import com.project.medtech.exception.NotFoundException;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.mapper.DrScheduleMapper;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.DrScheduleEntity;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DrScheduleRopository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.*;

@Service
@AllArgsConstructor
public class DrScheduleService {

    private final DrScheduleRopository repository;

    private final CheckListRepository checkListRepository;


    public List<CheckListDto> findMyReserved(Long docID, Date date) {
        List<CheckListEntity> list = checkListRepository.findByReservedByMe(docID, date);

        List<CheckListDto> lis2 = new ArrayList<>();

        list.forEach(x -> lis2.add(CheckListMapper.EntityToDto(x)));

        return lis2;
    }

    public List<Pickedtime> getGetAllReservedTime(Long patientID, Long doctorID, Date date) {
        List<Pickedtime> pcktTime = getAllpickedTime(patientID, doctorID, date);

        return getPickedtimes(doctorID, date, pcktTime);
    }

    private List<Pickedtime> getPickedtimes(Long doctorID, Date date, List<Pickedtime> pcktTime) {
        List<Time> times = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        List<Time> timess = new ArrayList<>();

        for (Pickedtime pickedtime : pcktTime) {
            timess.add(pickedtime.getTime());
        }

        times.removeAll(timess);

        for (Time time : times) {
            pcktTime.add(new Pickedtime(time, Colors.GRAY, date));
        }

        pcktTime.sort(Comparator.comparingInt(ob -> ob.getTime().getHours()));

        return pcktTime;
    }

    public List<Pickedtime> getGetAllReservedTimeW(Long patientID, Long doctorID, Date date) {
        List<Pickedtime> pcktTime = getAllpickedTimeW(patientID, doctorID, date);

        return getPickedtimes(doctorID, date, pcktTime);
    }

    public List<Pickedtime> getAllpickedTime(Long patientID, Long doctorID, Date date) {
        List<CheckListDto> chlist = findMyReserved(doctorID, date);

        Set<Pickedtime> pktTime = new HashSet<>();

        List<Time> schedule = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {
                    Pickedtime pickedtime = new Pickedtime(time, Colors.RED, date);

                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {
                    Pickedtime pickedtime = new Pickedtime(time, Colors.BLUE, date);

                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }

    public List<Time> doctorScheduleTime(Long id, DayOfWeek day) {
        DrScheduleDTO doctorScheduleDto = findByDocIdAndDay(id, day).get();

        List<Time> time_list = new ArrayList<>();

        for (int i = 0; i < doctorScheduleDto.getTime_end().getHours() - doctorScheduleDto.getTime_start().getHours(); i++) {
            Time time = new Time(doctorScheduleDto.getTime_start().getHours() + i, 0, 0);
            time_list.add(time);
        }

        return time_list;
    }

    public List<Pickedtime> getAllpickedTimeW(Long patientID, Long doctorID, Date date) {
        List<CheckListDto> chlist = findMyReserved(doctorID, date);

        Set<Pickedtime> pktTime = new HashSet<>();

        List<Time> schedule = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {
                    Pickedtime pickedtime = new Pickedtime(time, Colors.GREEN, date);

                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {
                    Pickedtime pickedtime = new Pickedtime(time, Colors.GREEN, date);

                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }

    public List<ReservedDayDTO> getReservedDateWeb(Long patientID, Long doctorID, Date date) {
        List<CheckListDto> chlist = findMyReserved(doctorID, date);

        Set<ReservedDayDTO> pktTime = new HashSet<>();

        List<Time> schedule = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {
                    ReservedDayDTO pickedtime = new ReservedDayDTO(time, Colors.GREEN);

                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {
                    ReservedDayDTO pickedtime = new ReservedDayDTO(time, Colors.GREEN);

                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }

    public List<ReservedDayDTO> getReservedDateF(Long patientID, Long doctorID, Date date) {
        List<CheckListDto> chlist = findMyReserved(doctorID, date);

        Set<ReservedDayDTO> pktTime = new HashSet<>();

        List<Time> schedule = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {
                    ReservedDayDTO pickedtime = new ReservedDayDTO(time, Colors.RED);

                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {
                    ReservedDayDTO pickedtime = new ReservedDayDTO(time, Colors.BLUE);

                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }

    public List<ReservedDayWDTO> getReservedDateW(Long patientID, Long doctorID, Date date) {
        List<CheckListDto> chlist = findMyReserved(doctorID, date);

        Set<ReservedDayWDTO> pktTime = new HashSet<>();

        List<Time> schedule = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {
                    ReservedDayWDTO pickedtime = new ReservedDayWDTO(time, Colors.GREEN, checkListDto);

                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {
                    ReservedDayWDTO pickedtime = new ReservedDayWDTO(time, Colors.GREEN, checkListDto);

                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }

    private List<ReservedDayWDTO> getReservedDayDTOSW(Long doctorID, Date date, List<ReservedDayWDTO> pcktTime) {
        List<Time> times = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(), date.getDate()).getDayOfWeek());

        List<Time> timess = new ArrayList<>();

        for (ReservedDayWDTO dto : pcktTime) {
            timess.add(dto.getTime());
        }

        times.removeAll(timess);

        for (Time time : times) {
            pcktTime.add(new ReservedDayWDTO(time, Colors.GRAY, new CheckListDto()));
        }

        pcktTime.sort(Comparator.comparingInt(ob -> ob.getTime().getHours()));

        return pcktTime;
    }


    public List<ReservedDayWDTO> getGetAllReservedTimeDateWeb(Long patientID, Long doctorID, Date date) {
        List<ReservedDayWDTO> pcktTime = getReservedDateW(patientID, doctorID, date);

        return getReservedDayDTOSW(doctorID, date, pcktTime);
    }


    public List<ReservedDayDTO> getGetAllReservedTimeDate(Long patientID, Long doctorID, Date date) {
        List<ReservedDayDTO> pcktTime = getReservedDateF(patientID, doctorID, date);

        return getReservedDayDTOS(doctorID, date, pcktTime);
    }

    private List<ReservedDayDTO> getReservedDayDTOS(Long doctorID, Date date, List<ReservedDayDTO> pcktTime) {
        List<Time> times = doctorScheduleTime(doctorID, LocalDate.of(date.getYear(), date.getMonth(),
                date.getDate()).getDayOfWeek());

        List<Time> timess = new ArrayList<>();

        for (ReservedDayDTO dto : pcktTime) {
            timess.add(dto.getTime());
        }

        times.removeAll(timess);

        for (Time time : times) {
            pcktTime.add(new ReservedDayDTO(time, Colors.GRAY));
        }

        pcktTime.sort(Comparator.comparingInt(ob -> ob.getTime().getHours()));

        return pcktTime;
    }

    public List<ReservedDateDTO> reservedDates(Long pID, Long drID, Date date) {
        YearMonth yearMonthObject = YearMonth.of(date.getYear(), date.getMonth());

        int daysInMonth = yearMonthObject.lengthOfMonth(); //28

        List<ReservedDateDTO> rdate = new ArrayList<>();

        for (int i = 1; i < daysInMonth + 1; i++) {
            int num = 0;

            int num2 = 0;

            try {
                for (ReservedDayDTO reservedDayDTO : getGetAllReservedTimeDate(pID, drID, new Date(date.getYear(),
                        date.getMonth(), i))) {
                    if (reservedDayDTO.getColors().equals(Colors.RED)) {
                        num = num + 1;
                    } else if (reservedDayDTO.getColors().equals(Colors.BLUE)) {
                        num2 = num2 + 1;
                    }

                }
            } catch (Exception e) {
                continue;
            }

            if (num > 0) {
                rdate.add(new ReservedDateDTO(Colors.RED, new Date(date.getYear(), date.getMonth(), i),
                        getGetAllReservedTimeDate(pID, drID, new Date(date.getYear(), date.getMonth(), i))));
            } else if (num2 == getGetAllReservedTimeDate
                    (pID, drID, new Date(date.getYear(), date.getMonth(), i)).size()) {
                rdate.add(new ReservedDateDTO(Colors.BLUE, new Date(date.getYear(), date.getMonth(), i),
                        getGetAllReservedTimeDate(pID, drID, new Date(date.getYear(), date.getMonth(), i))));
            } else {
                rdate.add(new ReservedDateDTO(Colors.GRAY, new Date(date.getYear(), date.getMonth(), i),
                        getGetAllReservedTimeDate(pID, drID, new Date(date.getYear(), date.getMonth(), i))));
            }

        }
        return rdate;
    }

    public List<ReservedDateWDTO> reservedDatesW(Long pID, Long drID, Date date) {
        YearMonth yearMonthObject = YearMonth.of(date.getYear(), date.getMonth());

        int daysInMonth = yearMonthObject.lengthOfMonth(); //28

        List<ReservedDateWDTO> rdate = new ArrayList<>();

        for (int i = 1; i < daysInMonth + 1; i++) {
            int num = 0;

            try {
                for (ReservedDayWDTO reservedDayWDTO : getGetAllReservedTimeDateWeb(pID, drID, new Date(date.getYear(), date.getMonth(), i))) {
                    if (reservedDayWDTO.getColors().equals(Colors.GREEN)) {
                        num = num + 1;
                    }
                }
            } catch (Exception e) {
                continue;
            }

            if (num == getGetAllReservedTimeDateWeb(pID, drID, new Date(date.getYear(), date.getMonth(), i)).size()) {
                rdate.add(new ReservedDateWDTO(Colors.GREEN, new Date(date.getYear(), date.getMonth(), i), getGetAllReservedTimeDateWeb(pID, drID, new Date(date.getYear(), date.getMonth(), i))));
            } else {
                rdate.add(new ReservedDateWDTO(Colors.GRAY, new Date(date.getYear(), date.getMonth(), i), getGetAllReservedTimeDateWeb(pID, drID, new Date(date.getYear(), date.getMonth(), i))));
            }

        }
        return rdate;
    }

    public List<DrScheduleDTO> getAllSchedulesByDocID(long id) {
        List<DrScheduleEntity> list = repository.findAllDoctorSchedules(id);

        List<DrScheduleDTO> list2 = new ArrayList<>();

        for (DrScheduleEntity entity : list) {
            list2.add(DrScheduleMapper.EntityToDto(new DrScheduleEntity(entity.getId(), entity.getDayOfWeek().minus(2L), entity.getTime_end(), entity.getTime_start(), entity.getDoctor())));
        }

        return list2;
    }


    public DrScheduleDTO save(DrScheduleDTO dto) {
        DrScheduleEntity doctorSchedule = DrScheduleMapper.DtoToEntity(dto);

        repository.save(doctorSchedule);

        return DrScheduleMapper.EntityToDto(doctorSchedule);
    }

    public List<CheckListDto> getFutureAppointment(long id) {
        List<CheckListEntity> list = checkListRepository.findAllByPatientID(id);

        List<CheckListDto> list2 = new ArrayList<>();

        for (CheckListEntity check : list) {
            Date date = new Date(LocalDate.now().getYear(), LocalDate.now().getMonth().getValue(), LocalDate.now().getDayOfMonth());

            Date date1 = (Date) check.getDate();

            if (date.compareTo(date1) > 0)
                list2.add(CheckListMapper.EntityToDto(check));
        }
        return list2;
    }

    public List<DrScheduleDTO> getAllDoctorSchedules() {
        List<DrScheduleEntity> list = repository.findAll();

        List<DrScheduleDTO> listDto = new ArrayList<>();

        for (DrScheduleEntity entity : list) {
            listDto.add(DrScheduleMapper.EntityToDto(entity));
        }

        return listDto;
    }

    public Optional<DrScheduleDTO> findByDocIdAndDay(long id, DayOfWeek day) {
        DrScheduleEntity doctorSchedule = repository.findByDocIDAndDay(id, day).orElseThrow(() -> new NotFoundException("Doctor doesn's work this day : " + id));

        return Optional.of(DrScheduleMapper.EntityToDto(doctorSchedule));
    }

    public void delete(long id) {
        DrScheduleEntity schedule = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("No Doctor Schedule with ID : " + id));

        repository.delete(schedule);
    }


    public DrScheduleDTO update(long id, DrScheduleDTO dto) {
        DrScheduleEntity schedule = repository.findById(id).orElseThrow(() -> new NotFoundException("No DoctorS chedule with ID : " + id));

        DrScheduleEntity schedule1 = DrScheduleMapper.DtoToEntity(dto);
        schedule1.setId(schedule.getId());

        return DrScheduleMapper.EntityToDto(repository.save(schedule1));
    }

    public Optional<DrScheduleDTO> findById(long id) {
        DrScheduleEntity doctorSchedule = repository.findById(id).orElseThrow(() -> new NotFoundException("No Doctor Schedule with ID : " + id));

        return Optional.of(DrScheduleMapper.EntityToDto(doctorSchedule));
    }

}
