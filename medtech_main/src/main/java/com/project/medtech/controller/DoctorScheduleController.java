package com.project.medtech.controller;

import com.project.medtech.dto.*;
import com.project.medtech.service.DrScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/drSchedule")
@AllArgsConstructor
@Api("REST APIs related to DrSchedule entity")
public class DoctorScheduleController {

    private final DrScheduleService service;


    @ApiOperation(value = " получения расписаний месяца с зянятыми днями и часами (Веб)")
    @GetMapping(value = "/getAllReservdDateTimeW/{pId}/{docID}/{date}")
    ResponseEntity<List<ReservedDateWDTO>> getAllReserveDateW(@PathVariable("pId") long id, @PathVariable("docID") long docID, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(service.reservedDatesW(id, docID, date));
    }


    @ApiOperation(value = " получения расписаний месяца с зянятыми днями и часами (Флаттер)")
    @GetMapping(value = "/getAllReservdDateTimeF/{pId}/{docID}/{date}")
    ResponseEntity<List<ReservedDateDTO>> getAllReserveDate(@PathVariable("pId") long id, @PathVariable("docID") long docID, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(service.reservedDates(id, docID, date));
    }


    @ApiOperation(value = " получения расписаний определенного доктора по id")
    @GetMapping(value = "/getAllDoctSchdedules/{dID}")
    ResponseEntity<List<DrScheduleDTO>> getAllDrSchedulesByID(@PathVariable("dID") long id) {
        return ResponseEntity.ok().body(service.getAllSchedulesByDocID(id));
    }

    @ApiOperation(value = " получения расписания определенного доктора и " +
            "пациента по определенной дате (Флаттер)")
    @GetMapping(value = "/getAllReservdTimeF/{pId}/{docID}/{date}")
    ResponseEntity<List<Pickedtime>> getAllReservdTime(@PathVariable("pId") long id, @PathVariable("docID") long docID, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(service.getGetAllReservedTime(id, docID, date));
    }


    @ApiOperation(value = " получения расписания определенного доктора и " +
            "пациента по определенной дате (Веб)")
    @GetMapping(value = "/getAllReservdTimeW/{pId}/{docID}/{date}")
    ResponseEntity<List<Pickedtime>> getAllReservdTimeW(@PathVariable("pId") long id, @PathVariable("docID") long docID, @PathVariable("date") Date date) {
        return ResponseEntity.ok().body(service.getGetAllReservedTimeW(id, docID, date));
    }

    @ApiOperation(value = " получения будущих приёмов которые запланировались," +
            " но еще прошли по id пациента (Флатер)")
    @GetMapping(value = "/appointments/{pID}")
    ResponseEntity<List<CheckListDto>> futureAppoitments(@PathVariable("pID") long id) {
        return ResponseEntity.ok(service.getFutureAppointment(id));
    }

    @ApiOperation(value = " получения всех расписаний всех докторов (ВЕБ)")
    @GetMapping(value = "/getAllSchedules")
    ResponseEntity<List<DrScheduleDTO>> getAll() {
        return ResponseEntity.ok(service.getAllDoctorSchedules());
    }


    @ApiOperation(value = " получения расписания доктора по id расписания (ВЕБ)")
    @GetMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<DrScheduleDTO> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(service.findById(id).get());
    }

    @ApiOperation(value = " создание расписания доктора (ВЕБ)")
    @PostMapping(value = "/doctorSchedule")
    ResponseEntity<DrScheduleDTO> createDoctorSchedule(@RequestBody DrScheduleDTO dto) {
        return ResponseEntity.ok().body(service.save(dto));
    }

    @ApiOperation(value = " изменение расписания доктора (ВЕБ)")
    @PutMapping(value = "/doctorSchedule/{id}")
    ResponseEntity<DrScheduleDTO> updateDoctorSchedule(@PathVariable("id") long id, @RequestBody DrScheduleDTO dto) {
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @ApiOperation(value = " удаление расписания доктора по id (ВЕБ)")
    @DeleteMapping(value = "/deleteDoctorSchedule/{id}")
    ResponseEntity<Void> deleteDrSchedule(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
