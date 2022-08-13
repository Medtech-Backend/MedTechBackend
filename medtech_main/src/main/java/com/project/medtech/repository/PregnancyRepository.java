package com.project.medtech.repository;

import com.project.medtech.model.PregnancyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnancyRepository extends JpaRepository<PregnancyEntity, Long> {
}
