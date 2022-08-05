package com.project.medtech.repository;

import com.project.medtech.model.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    @Query("SELECT d FROM DoctorEntity d WHERE d.userEntity.userId = ?1")
    Optional<DoctorEntity> findDoctorByUser(Long id);

}
