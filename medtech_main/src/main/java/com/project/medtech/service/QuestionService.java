package com.project.medtech.service;


import com.project.medtech.dto.NewQuestionDto;
import com.project.medtech.dto.QuestionDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.QuestionMapper;
import com.project.medtech.model.QuestionEntity;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;


    public List<QuestionDto> getAllQuestion() {
        return questionRepository.findAllByStatus(Status.ACTIVE).stream()
                .map(QuestionMapper::EntityToDto)
                .collect(Collectors.toList());
    }

    public Optional<QuestionDto> findById(long id) {
        QuestionEntity questionEntity = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Question with ID : " + id));
        return Optional.of(QuestionMapper.EntityToDto(questionEntity));
    }

    public QuestionDto save(NewQuestionDto dto) {
        QuestionEntity questionEntity = QuestionMapper.NewDtoToEntity(dto);
        questionRepository.save(questionEntity);
        QuestionDto questionDto = QuestionMapper.EntityToDto(questionEntity);
        return questionDto;
    }

    public QuestionDto update(long id, NewQuestionDto dto) {
        QuestionEntity questionEntity = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Question with ID : " + id));
        QuestionEntity newQuestionEntity = QuestionMapper.NewDtoToEntity(dto);
        newQuestionEntity.setId(questionEntity.getId());
        return QuestionMapper.EntityToDto(questionRepository.save(newQuestionEntity));
    }

    public QuestionDto delete(long id) {
        QuestionEntity questionEntity = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Question with ID : " + id));
        questionEntity.setStatus(Status.BANNED);
        questionRepository.save(questionEntity);
        return QuestionMapper.EntityToDto(questionEntity);
    }

}