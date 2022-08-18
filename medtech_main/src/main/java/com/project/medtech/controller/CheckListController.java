package com.project.medtech.controller;

import com.project.medtech.dto.SimpleCheckListInfoDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.exporter.CheckListExcelExporter;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.service.CheckListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/checklist")
@Api("REST APIs related to `Checklist` Entity")
public class CheckListController {

    private final CheckListService checkListService;

    private final CheckListRepository checkListRepository;


    @ApiOperation(value = "получение чеклиста в виде excel файла по ID чеклиста (ВЕБ)")
    @GetMapping("/excel/export/{id}")
    public void exportToExcel(HttpServletResponse response,@ApiParam(value = "введите ID чеклиста") @PathVariable("id") long id) throws IOException {

        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());

        currentDateTime = currentDateTime.replaceAll(":", "-");

        String headerKey = "Content-Disposition ";

        String headerValue = "attachment; filename=checklists_" + currentDateTime + ".xlsx";

        response.setHeader(headerKey, headerValue);

        CheckListEntity checkListEntity = checkListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));

        CheckListExcelExporter excelExporter = new CheckListExcelExporter(checkListEntity);

        excelExporter.export(response);
    }

    @ApiOperation(value = "запись на приём и создание чек-листа (МОБ)")
    @PostMapping(value = "/create")
    ResponseEntity<SimpleCheckListInfoDto> createNewCheckList(@ApiParam(value = "введите данные чеклиста") @RequestBody SimpleCheckListInfoDto dto) {
        return ResponseEntity.ok().body(checkListService.save(dto));
    }

    @ApiOperation(value = "получение всех чек-листов (ВЕБ)")
    @GetMapping(value = "/get-all")
    ResponseEntity<List<SimpleCheckListInfoDto>> getAll() {
        return ResponseEntity.ok(checkListService.getAllCheckLists());
    }

    @ApiOperation(value = "получение чек-листа по ID (ВЕБ)")
    @GetMapping(value = "/get/{id}")
    ResponseEntity<SimpleCheckListInfoDto> getById(@ApiParam(value = "введите ID чеклиста") @PathVariable("id") long id) {
        return ResponseEntity.ok().body(checkListService.findById(id).get());
    }


}
