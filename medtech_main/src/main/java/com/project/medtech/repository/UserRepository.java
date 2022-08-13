package com.project.medtech.repository;

import com.project.medtech.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.roleEntity.name = :role  AND " +
            "(u.firstName LIKE %:substring% OR u.lastName LIKE %:substring% OR u.middleName LIKE %:substring%) ")
    List<UserEntity> findAllByFio(String role, String substring);

    List<UserEntity> findAllByRoleEntityName(String role);

    UserEntity findByEmail(String email);

    Boolean existsByEmail(String email);

}
