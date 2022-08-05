package com.project.medtech.service;

import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.NewCheckListDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.model.AnswerEntity;
import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.QuestionEntity;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckListService {

    private final CheckListRepository repository;

    private final DoctorRepository doctorRepository;

    private final PatientRepository patientRepository;

    private final QuestionRepository questionRepository;


    public List<CheckListDto> getAllCheckLists() {
        List<CheckListEntity> list = repository.findAll();
        List<CheckListDto> listDto = new ArrayList<>();
        for (CheckListEntity checkListEntity : list) {
            listDto.add(CheckListMapper.EntityToDto(checkListEntity));
        }
        return listDto;
    }

    public Optional<CheckListDto> findById(long id) {
        CheckListEntity text = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        return Optional.of(CheckListMapper.EntityToDto(text));
    }

    public CheckListDto save(NewCheckListDto dto) {
        CheckListEntity checkListEntity = new CheckListEntity();
        checkListEntity.setDoctorEntity(doctorRepository.findById(dto.getDoctorId()).get());
        checkListEntity.setPatientEntity(patientRepository.findById(dto.getPatientId()).get());
        checkListEntity.setDate(dto.getDate());
        checkListEntity.setTime(dto.getTime());

        List<QuestionEntity> questionEntities = questionRepository.findAllByStatus(Status.ACTIVE);
        List<AnswerEntity> answerEntities = new ArrayList<AnswerEntity>();

        for (QuestionEntity questionEntity : questionEntities) {
            AnswerEntity ans = new AnswerEntity();
            ans.setQuestion(questionEntity.getQuestion());
            ans.setCheckListEntity(checkListEntity);
            answerEntities.add(ans);
        }

        checkListEntity.setAnswerEntities(answerEntities);

        repository.save(checkListEntity);
        return CheckListMapper.EntityToDto(checkListEntity);

    }

    public CheckListDto update(long id, CheckListDto dto) {
        CheckListEntity checkListEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        CheckListEntity checkListEntity1 = CheckListMapper.DtoToEntity(dto);
        checkListEntity1.setId(checkListEntity.getId());
        return CheckListMapper.EntityToDto(repository.save(checkListEntity1));
    }

    public CheckListDto delete(long id) {
        CheckListEntity checkListEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        repository.delete(checkListEntity);
        return CheckListMapper.EntityToDto(checkListEntity);
    }

}
