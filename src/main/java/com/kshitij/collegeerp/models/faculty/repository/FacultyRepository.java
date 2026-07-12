package com.kshitij.collegeerp.models.faculty.repository;

import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository
        extends JpaRepository<Faculty, Long>,
        JpaSpecificationExecutor<Faculty> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Faculty> findByEmployeeCode(String employeeCode);

    Optional<Faculty> findByUserId(Long userId);

    long countByStatus(FacultyStatus status);

    long countByDepartment(Department department);

    long countByDesignation(Designation designation);

    boolean existsByDepartmentAndDesignationAndStatus(
            Department department,
            Designation designation,
            FacultyStatus status
    );

    boolean existsByDepartmentAndDesignationAndStatusAndIdNot(
            Department department,
            Designation designation,
            FacultyStatus status,
            Long id
    );

    List<Faculty>findByDepartmentId(Long departmentId);

}