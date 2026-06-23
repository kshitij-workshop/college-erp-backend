package com.kshitij.collegeerp.models.assignment.repository;

import com.kshitij.collegeerp.models.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySubjectOfferingId(Long subjectOfferingId);
    List<Assignment> findBySubjectOfferingSectionId(Long sectionId);
}
