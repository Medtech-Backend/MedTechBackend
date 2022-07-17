package com.project.medtech.repository;

import com.project.medtech.model.Pregnancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnancyRepository extends JpaRepository<Pregnancy, Long> {
}
