package com.project.medtech.repository;

import com.project.medtech.dto.enums.Status;
import com.project.medtech.model.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findAllByStatus(Status status);

}
