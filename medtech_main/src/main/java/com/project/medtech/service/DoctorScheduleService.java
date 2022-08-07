package com.project.medtech.service;


import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.DoctorScheduleDto;
import com.project.medtech.dto.PickedtimeDTO;
import com.project.medtech.dto.enums.Colors;
import com.project.medtech.exception.NotFoundException;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.mapper.DoctorScheduleMapper;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.DoctorSchedule;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {


    private final DoctorScheduleRepository repository;

    private final CheckListRepository checkListRepository;


    public List<CheckListDto> findMyReserved(Long docID, Date date) {
        List<CheckListEntity> list = checkListRepository.findByReservedByMe(docID, date);
        List<CheckListDto> lis2 = new ArrayList<>();
        list.forEach(x -> lis2.add(CheckListMapper.EntityToDto(x)));

        return lis2;
    }

    public List<CheckListDto> getCheckListsByDocID(Long docID) {
        List<CheckListEntity> getByDoc = checkListRepository.findByDocID(docID);
        List<CheckListDto> list = new ArrayList<>();
        for (CheckListEntity checkList : getByDoc) {
            list.add(CheckListMapper.EntityToDto(checkList));
        }
        return list;
    }


    public List<PickedtimeDTO> getGetAllReservedTime(Long patientID, Long doctorID, Date date) {
        List<PickedtimeDTO> pcktTime = getAllpickedTime(patientID, doctorID, date);
        List<Time> times = doctorScheduleTime(doctorID, date);
        List<Time> timess = new ArrayList<>();

        for (PickedtimeDTO pickedtime : pcktTime) {
            timess.add(pickedtime.getTime());
        }
        times.removeAll(timess);

        for (Time time : times) {
            pcktTime.add(new PickedtimeDTO(time, Colors.GRAY));
        }
        pcktTime.sort(Comparator.comparingInt(ob -> ob.getTime().getHours()));

        return pcktTime;
    }

    public List<PickedtimeDTO> getAllpickedTime(Long patientID, Long doctorID, Date date) {

        List<CheckListDto> chlist = findMyReserved(doctorID, date);
        Set<PickedtimeDTO> pktTime = new HashSet<>();
        List<Time> schedule = doctorScheduleTime(doctorID, date);


        for (CheckListDto checkListDto : chlist) {
            for (Time time : schedule) {
                if (time.equals(checkListDto.getTime()) &&
                        Objects.equals(checkListDto.getPatientEntity().getId(), patientID)) {

                    PickedtimeDTO pickedtime = new PickedtimeDTO(time, Colors.RED);
                    pktTime.add(pickedtime);
                } else if (time.equals(checkListDto.getTime())) {

                    PickedtimeDTO pickedtime = new PickedtimeDTO(time, Colors.BLUE);
                    pktTime.add(pickedtime);
                }
            }
        }
        return new ArrayList<>(pktTime);
    }


    public List<CheckListDto> bookedTime(int doctorID) {
        List<CheckListDto> chlist = getAllCheckLists();
        Date docDate = new Date(findById(doctorID).get().getDayOfWeek().getYear(),
                findById(doctorID).get().getDayOfWeek().getMonth(),
                findById(doctorID).get().getDayOfWeek().getDate());

        Set<CheckListDto> set = new HashSet<>();
        for (CheckListDto checkListDto : chlist) {
            if (checkListDto.getDate().equals(docDate)) {
                set.add(checkListDto);
            }
        }

        List<CheckListDto> lsst = new ArrayList<>(set);
        lsst.sort(Comparator.comparingInt(ob -> ob.getTime().getHours()));
        return lsst;
    }


    public List<Time> doctorScheduleTime(Long id, Date date) {
        DoctorScheduleDto doctorScheduleDto = findByDocIdAndDate(id, date).get();
        List<Time> time_list = new ArrayList<>();
        for (int i = 0; i < doctorScheduleDto.getTime_end().getHours() - doctorScheduleDto.getTime_start().getHours(); i++) {
            Time time = new Time(doctorScheduleDto.getTime_start().getHours() + i, 0, 0);
            time_list.add(time);
        }
        return time_list;
    }

    public List<DoctorScheduleDto> getAllDoctorSchedules() {
        List<DoctorSchedule> list = repository.findAll();
        List<DoctorScheduleDto> listDto = new ArrayList<>();
        for (DoctorSchedule entity : list) {
            listDto.add(DoctorScheduleMapper.EntityToDto(entity));
        }
        return listDto;
    }

    public Optional<DoctorScheduleDto> findById(long id) {
        DoctorSchedule doctorSchedule = repository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("No Doctor Schedule with ID : " + id));

        return Optional.of(DoctorScheduleMapper.EntityToDto(doctorSchedule));
    }

    public List<DoctorScheduleDto> listOfSchedulesByDocId(long id) {
        List<DoctorScheduleDto> list1 = getAllDoctorSchedules();
        List<DoctorScheduleDto> list2 = getAllDoctorSchedules();
        for (DoctorScheduleDto dto : list1) {
            if (dto.getDoctor().getId() == id)
                list2.add(dto);
        }
        return list2;
    }

    public Optional<DoctorScheduleDto> findByDocIdAndDate(long id, Date date) {
        DoctorSchedule doctorSchedule = repository.findByDocIDAndDate(id, date)
                .orElseThrow(
                        () -> new NotFoundException("No Doctor Schedule with ID : " + id));

        return Optional.of(DoctorScheduleMapper.EntityToDto(doctorSchedule));
    }


    public DoctorScheduleDto save(DoctorScheduleDto dto) {
        DoctorSchedule doctorSchedule = DoctorScheduleMapper.DtoToEntity(dto);
        repository.save(doctorSchedule);
        DoctorScheduleDto dto1 = DoctorScheduleMapper.EntityToDto(doctorSchedule);
        return dto1;
    }

    public DoctorScheduleDto update(long id, DoctorScheduleDto dto) {
        DoctorSchedule schedule = repository.findById(id)
                .orElseThrow(
                () -> new NotFoundException("No DoctorS chedule with ID : " + id));

        DoctorSchedule schedule1 = DoctorScheduleMapper.DtoToEntity(dto);
        schedule1.setId(schedule.getId());
        return DoctorScheduleMapper.EntityToDto(repository.save(schedule1));
    }

    public void delete(long id) {
        DoctorSchedule schedule = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No Doctor Schedule with ID : " + id));
        repository.delete(schedule);
    }

    public List<CheckListDto> getAllCheckLists() {
        List<CheckListEntity> list = checkListRepository.findAll();
        List<CheckListDto> listDto = new ArrayList<>();
        for (CheckListEntity checkList : list) {
            listDto.add(CheckListMapper.EntityToDto(checkList));
        }
        return listDto;
    }

}
