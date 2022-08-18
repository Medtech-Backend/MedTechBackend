package com.project.medtech.repository;

import com.project.medtech.model.CheckListEntity;
import com.project.medtech.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CheckListRepository extends JpaRepository<CheckListEntity, Long> {

    List<CheckListEntity> findAllByDateOrderByTimeAsc(LocalDate date);

    @Query("SELECT ch FROM CheckListEntity ch WHERE ch.patientEntity = :patientEntity")
    List<CheckListEntity> findAllByPatientEntity(@RequestParam PatientEntity patientEntity);

    @Query("SELECT ch FROM CheckListEntity ch WHERE ch.patientEntity.id= :pID")
    List<CheckListEntity> findAllByPatientID(@RequestParam Long pID);

    @Query("SELECT ch FROM CheckListEntity ch WHERE ch.doctorEntity.id = :docID")
    List<CheckListEntity> findByDocID(@RequestParam Long docID);

    @Query("SELECT  ch FROM CheckListEntity ch WHERE ch.doctorEntity.id =:docID AND ch.date =:cDate ")
    List<CheckListEntity> findByReservedByMe(@RequestParam Long docID, LocalDate cDate);

    @Query("SELECT ch FROM CheckListEntity ch " +
            "WHERE ch.doctorEntity.id = :doctorId AND ch.date = :date AND ch.time = :time")
    Optional<CheckListEntity> findChecklistProfile(Long doctorId, LocalDate date, LocalTime time);

    @Query(
            value = "select concat(uD.lastName, ' ', substring(uD.firstName, 1, 1), " +
                    "'.', substring(uD.middleName, 1, 1), '.') as doctorFullName, " +
                    "concat(uP.lastName, ' ', substring(uP.firstName, 1, 1), " +
                    "'.', substring(uP.middleName, 1, 1), '.') as patientFullName, " +
                    "ch.date as dt, ch.time as tm " +
                    "from CheckListEntity ch " +
                    "inner join PatientEntity p " +
                    "on ch.patientEntity.id = p.id " +
                    "inner join UserEntity uP " +
                    "on p.userEntity.userId = uP.userId " +
                    "inner join DoctorEntity d " +
                    "on ch.doctorEntity.id = d.id " +
                    "inner join UserEntity uD " +
                    "on d.userEntity.userId = uD.userId " +
                    "where uD.firstName " +
                    "like %:fullName% or " +
                    "uD.lastName like %:fullName% or " +
                    "uD.middleName like %:fullName% " +
                    "order by uD.firstName, uD.firstName, uD.middleName"
    )
    List<Map<String, Object>> findByDoctorsFullName(String fullName);

}
