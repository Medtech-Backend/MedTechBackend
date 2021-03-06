package com.project.medtech.controller;

import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.NewCheckListDto;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.service.CheckListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/checklist")
@Api( "REST APIs related to `Checklist` Entity")
public class CheckListController {

    private final CheckListService checkListService;

    @ApiOperation(value = "запись на приём и создание чек-листа")
    @PostMapping(value="/create")
    ResponseEntity<CheckListDto> createNewCheckList(@RequestBody NewCheckListDto dto){
        return ResponseEntity.ok().body(checkListService.save(dto));
    }

    @ApiOperation(value = "получение всех чек-листов")
    @GetMapping(value="/get-all")
    ResponseEntity<List<CheckListDto>> getAll(){
        return ResponseEntity.ok(checkListService.getAllCheckLists());
    }

    @ApiOperation(value = "получение чек-листов по ID")
    @GetMapping(value="/get/{id}")
    ResponseEntity<CheckListDto> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(checkListService.findById(id).get());
    }

    @ApiOperation(value = "обновление чек-листа")
    @PutMapping(value="/update/{id}")
    ResponseEntity<CheckListDto> updateCheckList(@PathVariable("id") long id, @RequestBody CheckListDto dto) {
        return ResponseEntity.ok().body(checkListService.update(id, dto));
    }

    @ApiOperation(value = "удаление чек-листа по ID")
    @DeleteMapping(value="/delete/{id}")
    ResponseEntity<CheckListDto> deleteCheckList( @PathVariable("id") long id) {
        return ResponseEntity.ok(checkListService.delete(id));
    }

}
