package com.project.medtech.controller;


import com.project.medtech.dto.QuestionDto;
import com.project.medtech.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@Api( "REST APIs related to `Question` Entity")
public class QuestionController {

    private final QuestionService questionService;


    @ApiOperation(value = "вывод всех стандартных вопросов чек-листа")
    @GetMapping(value="/get-all")
    ResponseEntity<List<QuestionDto>> getAll(){
        return ResponseEntity.ok(questionService.getAllQuestion());
    }

    @ApiOperation(value = "вывод стандартного вопросы по ID")
    @GetMapping(value="/get/{id}")
    ResponseEntity<QuestionDto> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(questionService.findById(id).get());
    }

    @ApiOperation(value = "добавление нового стандартного вопроса")
    @PostMapping(value="/create")
    ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto dto) {
        return ResponseEntity.ok().body(questionService.save(dto));
    }

    @ApiOperation(value = "изменение стандартного вопроса по ID")
    @PutMapping(value="/update/{id}")
    ResponseEntity<QuestionDto> updateText(@PathVariable("id") long id, @RequestBody QuestionDto dto) {
        return ResponseEntity.ok().body(questionService.update(id, dto));
    }

    @ApiOperation(value = "удаление вопроса из стандартного чек-листа по ID")
    @DeleteMapping(value="/delete/{id}")
    ResponseEntity<QuestionDto> deleteText( @PathVariable("id") long id) {
        return ResponseEntity.ok(questionService.delete(id));
    }

}