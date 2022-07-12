package com.project.medtech.repository;

import com.project.medtech.model.Answer;
import com.project.medtech.model.CheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByCheckList(CheckList checkList);
}
