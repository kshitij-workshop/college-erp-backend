package com.kshitij.collegeerp.academic.batch.repository;

import com.kshitij.collegeerp.academic.batch.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByProgramId(Long programId);
    boolean existsByNameAndProgramId(String name, Long programId);
}


