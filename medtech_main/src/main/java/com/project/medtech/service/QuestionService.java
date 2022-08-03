package com.project.medtech.service;


import com.project.medtech.dto.QuestionDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.QuestionMapper;
import com.project.medtech.model.Question;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionDto> getAllQuestion() {
        List<Question> list = questionRepository.findAll();
        // TODO: 03.08.2022 Можно было так через стрим замапить
        // return questionRepository.findAll().stream().map(QuestionMapper::EntityToDto).collect(Collectors.toList());
        List<QuestionDto> listDto = new ArrayList<>();
        for(Question question : list ){
            listDto.add(QuestionMapper.EntityToDto(question));
        }
        return listDto;
    }


    public Optional<QuestionDto> findById(long id) {
        Question question = questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No Question with ID : "+id));
        return Optional.of(QuestionMapper.EntityToDto(question));
    }

    public QuestionDto save(QuestionDto dto) {
        Question question = QuestionMapper.DtoToEntity(dto);
        questionRepository.save(question);
        QuestionDto questionDto = QuestionMapper.EntityToDto(question);
        return questionDto;
    }

    public QuestionDto update(long id, QuestionDto dto){
        Question question = questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No Question with ID : "+id));
        Question newQuestion = QuestionMapper.DtoToEntity(dto);
        newQuestion.setId(question.getId());
        return QuestionMapper.EntityToDto(questionRepository.save(newQuestion));
    }

    public QuestionDto delete(long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No Question with ID : "+id));
        questionRepository.delete(question);
        return QuestionMapper.EntityToDto(question);
    }
}