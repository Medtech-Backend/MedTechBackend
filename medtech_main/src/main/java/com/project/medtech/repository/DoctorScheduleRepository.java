package com.project.medtech.repository;

import com.project.medtech.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    @Query("SELECT  ch FROM DoctorSchedule ch WHERE ch.doctor.id =:docID AND ch.dayOfWeek =:cDate")
    Optional<DoctorSchedule> findByDocIDAndDate(@RequestParam Long docID, Date cDate);


}
