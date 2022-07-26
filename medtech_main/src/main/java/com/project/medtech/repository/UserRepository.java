package com.project.medtech.repository;


import com.project.medtech.dto.enums.Role;
import com.project.medtech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.firstName, u.lastName, u.middleName DESC")
    List<User> findAll(Role role);

    User findByEmail(String email);

}
