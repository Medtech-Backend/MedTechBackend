package com.project.medtech.repository;

import com.project.medtech.model.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

    Optional<ContactEntity> findByText(String text);

}
