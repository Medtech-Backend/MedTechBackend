package com.project.medtech.service;

import com.project.medtech.dto.SimpleCheckListInfoDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.AlreadyExistsException;
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


    public List<SimpleCheckListInfoDto> getAllCheckLists() {
        List<CheckListEntity> list = repository.findAll();
        List<SimpleCheckListInfoDto> listDto = new ArrayList<>();
        for (CheckListEntity checkListEntity : list) {
            listDto.add(CheckListMapper.EntityToSimpleDto(checkListEntity));
        }
        return listDto;
    }

    public Optional<SimpleCheckListInfoDto> findById(long id) {
        CheckListEntity text = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        return Optional.of(CheckListMapper.EntityToSimpleDto(text));
    }

    public SimpleCheckListInfoDto save(SimpleCheckListInfoDto dto) {
        Boolean checkIfExists = repository
                .existsByDateAndTimeAndDoctorEntityId(dto.getDate(), dto.getTime(), dto.getDoctorId());

        if(checkIfExists) {
            throw new AlreadyExistsException("The given date and our is already booked. Choose another time.");
        }

        CheckListEntity checkListEntity = new CheckListEntity();

        checkListEntity.setDoctorEntity(doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new ResourceNotFoundException("No doctor with ID : " + dto.getDoctorId())));
        checkListEntity.setPatientEntity(patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("No patient with ID : " + dto.getPatientId())));
        checkListEntity.setDate(dto.getDate());
        checkListEntity.setTime(dto.getTime());

        List<QuestionEntity> questionEntities = questionRepository.findAllByStatus(Status.ACTIVE);
        List<AnswerEntity> answerEntities = new ArrayList<>();

        for (QuestionEntity questionEntity : questionEntities) {
            AnswerEntity ans = new AnswerEntity();
            ans.setQuestion(questionEntity.getQuestion());
            ans.setCheckListEntity(checkListEntity);
            answerEntities.add(ans);
        }

        checkListEntity.setAnswerEntities(answerEntities);

        repository.save(checkListEntity);
        return dto;

    }

}
