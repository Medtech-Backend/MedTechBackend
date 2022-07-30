package com.project.medtech.service;

import com.project.medtech.dto.CheckListDto;
import com.project.medtech.dto.NewCheckListDto;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.exporter.CheckListExcelExporter;
import com.project.medtech.exporter.PatientExcelExporter;
import com.project.medtech.mapper.CheckListMapper;
import com.project.medtech.model.Answer;
import com.project.medtech.model.CheckList;
import com.project.medtech.model.Question;
import com.project.medtech.repository.CheckListRepository;
import com.project.medtech.repository.DoctorRepository;
import com.project.medtech.repository.PatientRepository;
import com.project.medtech.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckListService {


    private final CheckListRepository checkListRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final QuestionRepository questionRepository;


    public void exportToExcel(HttpServletResponse response, Long checkListId) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition ";
        String headerValue = "attachment; filename=checklists_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        CheckList checkList = checkListRepository.findById(checkListId).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + checkListId));

        CheckListExcelExporter excelExporter = new CheckListExcelExporter(checkList);
        excelExporter.export(response);

    }

    public List<CheckListDto> getAllCheckLists() {
        List<CheckList> list = checkListRepository.findAll();
        List<CheckListDto> listDto = new ArrayList<>();
        for (CheckList checkList : list) {
            listDto.add(CheckListMapper.EntityToDto(checkList));
        }
        return listDto;
    }

    public Optional<CheckListDto> findById(long id) {
        CheckList text = checkListRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        return Optional.of(CheckListMapper.EntityToDto(text));
    }

    public CheckListDto save(NewCheckListDto dto) {
        CheckList checkList = new CheckList();
        checkList.setDoctor(doctorRepository.findById(dto.getDoctorId()).get());
        checkList.setPatient(patientRepository.findById(dto.getPatientId()).get());
        checkList.setDate(dto.getDate());
        checkList.setTime(dto.getTime());

        List<Question> questions = questionRepository.findAllByStatus(Status.ACTIVE);
        List<Answer> answers = new ArrayList<Answer>();

        for (Question question : questions) {
            Answer ans = new Answer();
            ans.setQuestion(question.getQuestion());
            ans.setCheckList(checkList);
            answers.add(ans);
        }

        checkList.setAnswers(answers);

        checkListRepository.save(checkList);
        return CheckListMapper.EntityToDto(checkList);

    }

    public CheckListDto update(long id, CheckListDto dto) {
        CheckList checkList = checkListRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        CheckList checkList1 = CheckListMapper.DtoToEntity(dto);
        checkList1.setId(checkList.getId());
        return CheckListMapper.EntityToDto(checkListRepository.save(checkList1));
    }

    public CheckListDto delete(long id) {
        CheckList checkList = checkListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No CheckList with ID : " + id));
        checkListRepository.delete(checkList);
        return CheckListMapper.EntityToDto(checkList);
    }

}
