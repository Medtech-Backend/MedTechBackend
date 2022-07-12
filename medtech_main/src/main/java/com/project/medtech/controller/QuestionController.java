package com.project.medtech.controller;


import com.project.medtech.dto.QuestionDto;
import com.project.medtech.service.QuestionService;
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
@RequestMapping("/api/v1/question")
@Api( "REST APIs related to standard questions Entity")
public class QuestionController {


    private final QuestionService questionService;

    @ApiOperation(value = "вывод всех стандартных вопросов чек-листа")
    @GetMapping(value="/questions")
    ResponseEntity<List<QuestionDto>> getAll(){
        return ResponseEntity.ok(questionService.getAllQuestion());
    }

    @ApiOperation(value = "вывод стандартного вопросы по ID")
    @GetMapping(value="/question/{id}")
    ResponseEntity<QuestionDto> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(questionService.findById(id).get());
    }

    @ApiOperation(value = "добавление нового стандартного вопроса")
    @PostMapping(value="/question")
    ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto dto) {
        return ResponseEntity.ok().body(questionService.save(dto));
    }

    @ApiOperation(value = "изменение стандартного вопроса по ID")
    @PutMapping(value="/question/{id}")
    ResponseEntity<QuestionDto> updateText(@PathVariable("id") long id, @RequestBody QuestionDto dto) {
        return ResponseEntity.ok().body(questionService.update(id, dto));
    }

    @ApiOperation(value = "удаление вопроса из стандартного чек-листа по ID")
    @DeleteMapping(value="/question/{id}")
    ResponseEntity<Void> deleteText( @PathVariable("id") long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}