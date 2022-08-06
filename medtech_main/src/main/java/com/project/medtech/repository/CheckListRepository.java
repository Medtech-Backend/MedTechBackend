package com.project.medtech.repository;

import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Repository
public interface CheckListRepository extends JpaRepository<CheckListEntity, Long> {

    @Query("SELECT ch FROM CheckListEntity ch WHERE ch.patientEntity = :patientEntity")
    List<CheckListEntity> findAllByPatientEntity(@RequestParam PatientEntity patientEntity);

    @Query("SELECT ch FROM CheckListEntity ch WHERE ch.doctorEntity.id = :docID")
    List<CheckListEntity> findByDocID(@RequestParam Long docID);

    @Query("SELECT  ch FROM CheckListEntity ch WHERE ch.doctorEntity.id =:docID AND ch.date =:cDate ")
    List<CheckListEntity> findByReservedByMe(@RequestParam Long docID, Date cDate);

}
