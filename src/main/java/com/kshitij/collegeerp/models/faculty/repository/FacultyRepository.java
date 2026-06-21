package com.kshitij.collegeerp.models.faculty.repository;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByEmployeeCode(String employeeCode);
    Optional<Faculty> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);
    List<Faculty> findByDepartmentId(Long departmentId);

    boolean existsByDepartmentIdAndDesignationAndStatus(
            @NotNull(message = "Department ID is required")
            Long departmentId,
            Designation designation, FacultyStatus facultyStatus);

    boolean existsByDepartmentIdAndDesignationAndStatusAndIdNot(
            @NotNull(message = "Department ID is required")
            Long departmentId, Designation designation,
            FacultyStatus facultyStatus, Long id);
}
