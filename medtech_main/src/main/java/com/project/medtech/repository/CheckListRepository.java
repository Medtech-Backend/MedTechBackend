package com.project.medtech.repository;

import com.project.medtech.model.CheckList;
import com.project.medtech.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface CheckListRepository extends JpaRepository<CheckList, Long> {

}
