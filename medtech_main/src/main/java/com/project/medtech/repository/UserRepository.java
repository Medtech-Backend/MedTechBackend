package com.project.medtech.repository;

import com.project.medtech.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByRoleEntityName(String role);

    UserEntity findByEmail(String email);

    Boolean existsByEmail(String email);

}
