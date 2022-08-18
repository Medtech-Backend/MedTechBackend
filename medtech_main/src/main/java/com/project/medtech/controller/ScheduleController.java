package com.project.medtech.controller;

import com.project.medtech.dto.CheckListPlannedDto;
import com.project.medtech.dto.CheckListProfileDto;
import com.project.medtech.dto.ScheduleDateStatusDto;
import com.project.medtech.dto.ScheduleDayDto;
import com.project.medtech.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
@Api( "REST APIs related to `Schedule` Entity")
public class ScheduleController {

    private final ScheduleService scheduleService;


    @ApiOperation(value = "получение сегодняшних записей (ВЕБ)")
    @GetMapping("/get-all-for-today")
    private ResponseEntity<List<CheckListPlannedDto>> getTodaysPlannedCheckLists() {
        return ResponseEntity.ok(scheduleService.getTodaysPlannedCheckLists());
    }

    @ApiOperation(value = "получение записей по ФИО доктора (ВЕБ)")
    @GetMapping("/get-all-by-doctors-full-name/{doctorFullName}")
    private ResponseEntity<List<CheckListPlannedDto>> getPlannedCheckLists(@PathVariable String doctorFullName) {
        return ResponseEntity.ok(scheduleService.getPlannedCheckLists(doctorFullName));
    }

    @ApiOperation(value = "получение записей по айди доктора, году и месяцу (ВЕБ)")
    @GetMapping ("/get-all-by-doctor-year-month/{doctorId}/{year}/{month}")
    private ResponseEntity<List<ScheduleDateStatusDto>>getMonthSchedule(@PathVariable Long doctorId,
                                                                                @PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(scheduleService.getMonthSchedule(doctorId, year, month));
    }

    @ApiOperation(value = "получение записей одного дня по айди доктора и дате (ВЕБ)")
    @GetMapping ("/get-by-doctor-date/{doctorId}/{date}")
    private ResponseEntity<ScheduleDayDto> getDaySchedule(@PathVariable Long doctorId,
                                                          @PathVariable String date) {
        return ResponseEntity.ok(scheduleService.getDaySchedule(doctorId, date));
    }

    @ApiOperation(value = "получение профиля чеклиста по айди доктора, дате и времени (ВЕБ)")
    @GetMapping("/get-by-doctor-date-time/{doctorId}/{localDate}/{localTime}")
    public ResponseEntity<CheckListProfileDto> getCheckListProfile(@PathVariable Long doctorId,
                           @PathVariable String localDate, @PathVariable String localTime) {
        return ResponseEntity.ok(scheduleService.getCheckListProfile(doctorId, localDate, localTime));
    }

}
