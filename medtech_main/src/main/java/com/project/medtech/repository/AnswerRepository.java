package com.project.medtech.repository;

import com.project.medtech.model.AnswerEntity;
import com.project.medtech.model.CheckListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    List<AnswerEntity> findAllByCheckListEntity(CheckListEntity checkListEntity);

}
