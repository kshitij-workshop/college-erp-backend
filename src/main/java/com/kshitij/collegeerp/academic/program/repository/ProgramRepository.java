package com.kshitij.collegeerp.academic.program.repository;

import com.kshitij.collegeerp.academic.program.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByCode(String code);
    boolean existsByCode(String code);
    List<Program> findByDepartmentId(Long id);
}
