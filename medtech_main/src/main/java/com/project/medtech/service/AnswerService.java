package com.project.medtech.service;

import com.project.medtech.dto.AnswerDto;
import com.project.medtech.dto.AnswerForQuestionDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.AnswerMapper;
import com.project.medtech.model.AnswerEntity;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.repository.AnswerRepository;
import com.project.medtech.repository.CheckListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository repository;

    private final CheckListRepository checkListRepository;


    public List<AnswerDto> answer(List<AnswerForQuestionDto> dtoList) {
        List<AnswerDto> response = new ArrayList<>();
        for (AnswerForQuestionDto dto : dtoList) {
            AnswerEntity answerEntity = repository.findById(dto.getAnswerId()).orElseThrow(() -> new ResourceNotFoundException("No answer with ID : " + dto.getAnswerId()));
            answerEntity.setIndicators(dto.getIndicators());
            answerEntity.setDescription(dto.getDescription());
            repository.save(answerEntity);
            response.add(AnswerMapper.EntityToDto(answerEntity));
        }

        return response;
    }

    public List<AnswerDto> getAllAnswers() {
        List<AnswerEntity> list = repository.findAll();
        List<AnswerDto> listDto = new ArrayList<>();
        for (AnswerEntity answerEntity : list) {
            listDto.add(AnswerMapper.EntityToDto(answerEntity));
        }
        return listDto;
    }

    public Optional<AnswerDto> findById(long id) {
        AnswerEntity answerEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No answer with ID : " + id));
        return Optional.of(AnswerMapper.EntityToDto(answerEntity));
    }


    public AnswerDto save(AnswerDto dto) {
        AnswerEntity answerEntity = AnswerMapper.DtoToEntity(dto);
        repository.save(answerEntity);
        AnswerDto answerDto = AnswerMapper.EntityToDto(answerEntity);
        return answerDto;
    }


    public AnswerDto update(long id, AnswerDto dto) {
        AnswerEntity answerEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No answer with ID : " + id));
        AnswerEntity newAnswerEntity = AnswerMapper.DtoToEntity(dto);
        newAnswerEntity.setId(answerEntity.getId());
        return AnswerMapper.EntityToDto(repository.save(newAnswerEntity));
    }

    public List<AnswerDto> getAllAnswersByCheckList(long id) {
        CheckListEntity checkListEntity = checkListRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        List<AnswerEntity> list = repository.findAllByCheckListEntity(checkListEntity);
        List<AnswerDto> listDto = new ArrayList<>();
        for (AnswerEntity answerEntity : list) {
            listDto.add(AnswerMapper.EntityToDto(answerEntity));
        }
        return listDto;
    }

}

