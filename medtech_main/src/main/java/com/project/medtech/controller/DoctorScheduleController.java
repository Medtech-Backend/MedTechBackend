package com.project.medtech.controller;


import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.DoctorScheduleDto;
import com.project.medtech.dto.PickedtimeDTO;
import com.project.medtech.service.DoctorScheduleService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class DoctorScheduleController {


    private final DoctorScheduleService doctorScheduleService;


    @ApiOperation(value = "получение всех чеклистов по id доктора")
    @GetMapping(value = "/getCheckListsByDocId/{id}")
    ResponseEntity<List<CheckListDto>> getAllCheckListByDocId(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(doctorScheduleService.getCheckListsByDocID(id));

    }

    @ApiOperation(value = "получение всех расписаний")
    @GetMapping(value = "/doctorSchedule")
    ResponseEntity<List<DoctorScheduleDto>> getAll() {
        return ResponseEntity.ok(doctorScheduleService.getAllDoctorSchedules());
    }

    @ApiOperation(value = "получение всех времени работы по дате и id доктора")
    @GetMapping(value = "/doctorScheduleTime/{id}/{date}")
    ResponseEntity<List<Time>> getDoctorScheduleTime(@PathVariable("id") long id, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(doctorScheduleService.doctorScheduleTime(id, date));
    }

    @ApiOperation(value = "получение чеклистов по id доктора")
    @GetMapping(value = "/getAllCheckListByDocID/{id}")
    ResponseEntity<List<CheckListDto>> getCheckListByDoc(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(doctorScheduleService.getCheckListsByDocID(id));
    }

    @ApiOperation(value = "получение свободного,занятого времени времени по id пациента, по id врача и дате")
    @GetMapping(value = "/getAllReservedTime/{pId}/{docID}/{date}")
    ResponseEntity<List<PickedtimeDTO>> getAllReservedTime(@PathVariable("pId") long id, @PathVariable("docID") long docID, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(doctorScheduleService.getGetAllReservedTime(id, docID, date));
    }

    @ApiOperation(value = "получения чеклистов в id доктора и дате")
    @GetMapping(value = "/getMyReserved/{docID}/{date}")
    ResponseEntity<List<CheckListDto>> getMyReserved(@PathVariable("docID") long docId, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(doctorScheduleService.findMyReserved(docId, date));
    }

    @ApiOperation(value = "получние всех занятых времен")
    @GetMapping(value = "/getAllBookedTime/{id}")
    ResponseEntity<List<CheckListDto>> getAllBookedTime(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(doctorScheduleService.bookedTime((int) id));
    }

    @ApiOperation(value = "получение расписаний доктора ")
    @GetMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<DoctorScheduleDto> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(doctorScheduleService.findById(id).get());
    }

    @ApiOperation(value = "получение расписаний доктора по id доктора ")
    @GetMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<List<DoctorScheduleDto>> listOfSchedulesByDocId(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(doctorScheduleService.listOfSchedulesByDocId(id));
    }

    @ApiOperation(value = "создания расписания доктора")
    @PostMapping(value = "/doctorSchedule")
    ResponseEntity<DoctorScheduleDto> createDoctorSchedule(@RequestBody DoctorScheduleDto dto) {
        return ResponseEntity.ok().body(doctorScheduleService.save(dto));
    }

    @ApiOperation(value = "изменения расписания доктора")
    @PutMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<DoctorScheduleDto> updateDoctorSchedule(@PathVariable("id") long id, @RequestBody DoctorScheduleDto dto) {
        return ResponseEntity.ok().body(doctorScheduleService.update(id, dto));
    }

    @ApiOperation(value= "удаления расписания доктора по ID")
    @DeleteMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<Void> deleteDoctorSchedule(@PathVariable("id") long id) {
        doctorScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
