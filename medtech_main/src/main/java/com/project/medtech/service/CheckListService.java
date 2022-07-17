package com.project.medtech.service;

import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.NewCheckListDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.model.Answer;
import com.project.medtech.model.CheckList;
import com.project.medtech.model.Question;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<CheckList> list = repository.findAll();
        List<CheckListDto> listDto = new ArrayList<>();
        for(CheckList checkList : list ){
            listDto.add(CheckListMapper.EntityToDto(checkList));
        }
        return listDto;
    }

    public Optional<CheckListDto> findById(long id) {
        CheckList text = repository.findById(id).orElseThrow(()->new ResourceNotFoundException("No CheckList with ID : "+id));
        return Optional.of(CheckListMapper.EntityToDto(text));
    }

    public CheckListDto save(NewCheckListDto dto){
        CheckList checkList = new CheckList();
        checkList.setDoctor(doctorRepository.findById(dto.getDoctorId()).get());
        checkList.setPatient(patientRepository.findById(dto.getPatientId()).get());
        checkList.setDate(dto.getDate());
        checkList.setTime(dto.getTime());

        List<Question> questions = questionRepository.findAllByStatus(Status.ACTIVE);
        List<Answer> answers = new ArrayList<Answer>();

        for(Question question : questions){
            Answer ans = new Answer();
            ans.setQuestion(question.getQuestion());
            ans.setCheckList(checkList);
            answers.add(ans);
        }

        checkList.setAnswers(answers);

        repository.save(checkList);
        return CheckListMapper.EntityToDto(checkList);

    }

    public CheckListDto update(long id, CheckListDto dto){
        CheckList checkList = repository.findById(id).orElseThrow(()->new ResourceNotFoundException("No CheckList with ID : "+id));
        CheckList checkList1 = CheckListMapper.DtoToEntity(dto);
        checkList1.setId(checkList.getId());
        return CheckListMapper.EntityToDto(repository.save(checkList1));
    }

    public void delete(long id) {
        CheckList checkList = repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No CheckList with ID : "+id));
        repository.delete(checkList);
    }

}
