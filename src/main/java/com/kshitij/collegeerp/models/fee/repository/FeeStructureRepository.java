package com.kshitij.collegeerp.models.fee.repository;

import com.kshitij.collegeerp.models.fee.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Optional<FeeStructure> findByProgramIdAndSemesterIdAndAcademicSession(
            Long programId, Long semesterId, String academicSession);
    List<FeeStructure> findByProgramId(Long programId);
}
