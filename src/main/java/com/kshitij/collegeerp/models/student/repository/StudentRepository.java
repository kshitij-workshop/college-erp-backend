package com.kshitij.collegeerp.models.student.repository;

import com.kshitij.collegeerp.models.student.entity.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

    Optional<Student> findBySectionId(Long sectionId);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

    List<Student> findByDepartmentId(Long departmentId);
    long countBySectionId(Long sectionId);

    List<Student> findBySectionIdOrderByRegistrationNumber(Long sectionId);
}