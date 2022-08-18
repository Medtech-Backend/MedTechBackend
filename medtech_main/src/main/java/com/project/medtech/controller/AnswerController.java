package com.project.medtech.controller;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.dto.AnswerForQuestionDto;
import com.project.medtech.service.AnswerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/answer")
@Api( "REST APIs related to `Answer` Entity")
public class AnswerController {

    private final AnswerService answerService;


    @ApiOperation(value = "заполнение показателей и описания чек-листа (ВЕБ)")
    @PutMapping(value="/fill-answers")
    ResponseEntity<List<AnswerDto>> answer(@ApiParam(value = "заполните ответы на вопросы") @RequestBody List<AnswerForQuestionDto> dtoList){
        return ResponseEntity.ok().body(answerService.answer(dtoList));
    }

    @ApiOperation(value = "поиск всех вопросов и ответов по ID чек-листа (ВЕБ)")
    @GetMapping(value = "/get-checklist-answers/{id}")
    ResponseEntity<List<AnswerDto>> getAllAnswersByCheckList(@ApiParam(value = "введите ID чеклиста") @PathVariable("id") long id) {
        return ResponseEntity.ok(answerService.getAllAnswersByCheckList(id));
    }

    @ApiOperation(value = "изменение ответ в чек-лист по ID ответа (ВЕБ)")
    @PutMapping(value="/update/{id}")
    ResponseEntity<AnswerDto> updateAnswer(@ApiParam(value = "введите ID ответа") @PathVariable("id") long id,@ApiParam(value = "введите обновлённый вариант ответа") @RequestBody AnswerDto dto) {
        return ResponseEntity.ok().body(answerService.update(id, dto));
    }

    @ApiOperation(value = "вывод всех ответов (ВЕБ)")
    @GetMapping(value="/get-all")
    ResponseEntity<List<AnswerDto>> getAllAnswers(){
        return ResponseEntity.ok(answerService.getAllAnswers());
    }

    @ApiOperation(value = "получение ответа по ID (ВЕБ)")
    @GetMapping(value="/get/{id}")
    ResponseEntity<AnswerDto> getById(@ApiParam(value = "введите ID ответа") @PathVariable("id") long id) {
        return ResponseEntity.ok().body(answerService.findById(id).get());
    }


}
