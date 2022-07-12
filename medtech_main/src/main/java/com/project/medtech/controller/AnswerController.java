package com.project.medtech.controller;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.dto.AnswerForQuestionDto;
import com.project.medtech.service.AnswerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/answer")
@RequiredArgsConstructor
@Api( "REST APIs related to answers Entity")
public class AnswerController {

    private final AnswerService answerService;

    @ApiOperation(value = "заполнение показателей и описания чек-листа")
    @PutMapping(value="/answerQuestions")
    ResponseEntity<List<AnswerDto>> answer(
            @RequestBody List<AnswerForQuestionDto> dtoList){
        return ResponseEntity.ok().body(answerService.answer(dtoList));
    }

    @ApiOperation(value = "поиск всех вопросов и ответов по ID чек-листа")
    @GetMapping(value = "/answersByCheckList/{id}")
    ResponseEntity<List<AnswerDto>> getAllAnswersByCheckList(@PathVariable("id") long id) {
        return ResponseEntity.ok(answerService.getAllAnswersByCheckList(id));
    }

    @ApiOperation(value = "изменить ответ на чек-лист")
    @PutMapping(value="/answer/{id}")
    ResponseEntity<AnswerDto> updateAnswer(@PathVariable("id") long id, @RequestBody AnswerDto dto) {
        return ResponseEntity.ok().body(answerService.update(id, dto));
    }

    @ApiOperation(value = "вывод всех ответов")
    @GetMapping(value="/answers")
    ResponseEntity<List<AnswerDto>> getAllAnswers(){
        return ResponseEntity.ok(answerService.getAllAnswers());
    }

    @ApiOperation(value = "вывод ответа по ID")
    @GetMapping(value="/answer/{id}")
    ResponseEntity<AnswerDto> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(answerService.findById(id).get());
    }

    @ApiOperation(value = "вывод ответа по ID")
    @DeleteMapping(value="/answer/{id}")
    ResponseEntity<Void> deleteAnswer( @PathVariable("id") long id) {
        answerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
