package com.project.medtech.service;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.dto.AnswerForQuestionDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.AnswerMapper;
import com.project.medtech.model.Answer;
import com.project.medtech.model.CheckList;
import com.project.medtech.repository.AnswerRepository;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository repository;
    private final CheckListRepository checkListRepository;


    public List<AnswerDto> answer(List<AnswerForQuestionDto> dtoList){
        List<AnswerDto> response = new ArrayList<>();
        for(AnswerForQuestionDto dto : dtoList){
            Answer answer = repository.findById(dto.getAnswerId()).orElseThrow(()->new ResourceNotFoundException("No answer with ID : "+dto.getAnswerId()));
            answer.setIndicators(dto.getIndicators());
            answer.setDescription(dto.getDescription());
            repository.save(answer);
            response.add(AnswerMapper.EntityToDto(answer));
        }

        return response;
    }

    public List<AnswerDto> getAllAnswers() {
        List<Answer> list = repository.findAll();
        List<AnswerDto> listDto = new ArrayList<>();
        for(Answer answer : list ){
            listDto.add(AnswerMapper.EntityToDto(answer));
        }
        return listDto;
    }

    public Optional<AnswerDto> findById(long id) {
        Answer answer = repository.findById(id).orElseThrow(()->new ResourceNotFoundException("No answer with ID : "+id));
        return Optional.of(AnswerMapper.EntityToDto(answer));
    }


    public AnswerDto save(AnswerDto dto) {
        Answer answer = AnswerMapper.DtoToEntity(dto);
        repository.save(answer);
        AnswerDto answerDto = AnswerMapper.EntityToDto(answer);
        return answerDto;
    }


    public AnswerDto update(long id, AnswerDto dto){
        Answer answer = repository.findById(id).orElseThrow(()->new ResourceNotFoundException("No answer with ID : "+id));
        Answer newAnswer = AnswerMapper.DtoToEntity(dto);
        newAnswer.setId(answer.getId());
        return AnswerMapper.EntityToDto(repository.save(newAnswer));
    }

    public void delete(long id) {
        Answer answer = repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No answer with ID : "+id));
        repository.delete(answer);
    }

    public List<AnswerDto> getAllAnswersByCheckList(long id) {
        CheckList checkList = checkListRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No CheckList with ID : "+id));
        List<Answer> list = repository.findAllByCheckList(checkList);
        List<AnswerDto> listDto = new ArrayList<>();
        for(Answer answer : list ){
            listDto.add(AnswerMapper.EntityToDto(answer));
        }
        return listDto;
    }
}

