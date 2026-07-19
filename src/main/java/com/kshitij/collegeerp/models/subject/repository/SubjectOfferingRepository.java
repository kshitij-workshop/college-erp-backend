package com.kshitij.collegeerp.models.subject.repository;

import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SubjectOfferingRepository extends JpaRepository<SubjectOffering, Long> {
    List<SubjectOffering> findByFacultyId(Long facultyId);
    List<SubjectOffering> findBySectionId(Long sectionId);

    boolean existsBySubjectIdAndSectionIdAndAcademicSession(
            Long subjectId, Long sectionId, String academicSession
    );

    List<SubjectOffering> findBySection_Semester_Batch_Program_Department_IdOrderBySubject_CodeAsc(Long departmentId);
}
