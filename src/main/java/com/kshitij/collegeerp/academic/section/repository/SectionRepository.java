package com.kshitij.collegeerp.academic.section.repository;

import com.kshitij.collegeerp.academic.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findBySemesterId(Long id);
    boolean existsByNameAndSemesterId(String name, Long semesterId);
}
