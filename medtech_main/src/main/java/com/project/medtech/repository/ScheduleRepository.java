package com.project.medtech.repository;

import com.project.medtech.model.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    Optional<ScheduleEntity> findByDayOfWeekAndDoctorId(String dayOfWeek, Long id);

}
