package com.project.medtech.repository;


import com.project.medtech.model.DrScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrScheduleRopository extends JpaRepository<DrScheduleEntity, Long> {
    @Query("SELECT  ch FROM DrScheduleEntity ch WHERE ch.doctor.id =:docID AND ch.dayOfWeek =:day")
    Optional<DrScheduleEntity> findByDocIDAndDay(@RequestParam Long docID, DayOfWeek day);



    @Query("SELECT dsc FROM DrScheduleEntity dsc WHERE dsc.doctor.id = :id")
    List<DrScheduleEntity> findAllDoctorSchedules(@RequestParam Long id);



}
