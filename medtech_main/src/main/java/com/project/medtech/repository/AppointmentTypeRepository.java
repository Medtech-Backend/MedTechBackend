package com.project.medtech.repository;

import com.project.medtech.model.AppointmentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentTypeRepository extends JpaRepository<AppointmentTypeEntity, Long> {

    Optional<AppointmentTypeEntity> findByName(String name);

}
