package com.project.medtech.repository;

import com.project.medtech.model.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

    ContentEntity findByWeekNumberAndOrder(Integer weekNumber, Integer order);

    List<ContentEntity> findAllByOrderByWeekNumberAscOrderAsc();

}
