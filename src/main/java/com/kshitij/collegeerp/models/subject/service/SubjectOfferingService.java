package com.kshitij.collegeerp.models.subject.service;

import com.kshitij.collegeerp.academic.section.dto.SectionResponse;
import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.academic.section.repository.SectionRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import com.kshitij.collegeerp.models.subject.dto.SubjectOfferingRequest;
import com.kshitij.collegeerp.models.subject.dto.SubjectOfferingResponse;
import com.kshitij.collegeerp.models.subject.entity.Subject;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import com.kshitij.collegeerp.models.subject.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectOfferingService {

    private final SubjectOfferingRepository subjectOfferingRepository;
    private final SubjectRepository subjectRepository;
    private final SectionRepository sectionRepository;
    private final FacultyRepository facultyRepository;

    @Transactional
    public SubjectOfferingResponse create(SubjectOfferingRequest request) {
        if (subjectOfferingRepository.existsBySubjectIdAndSectionIdAndAcademicSession(
                request.getSubjectId(), request.getSectionId(), request.getAcademicSession())) {
            throw new RuntimeException(
                    "This subject is already offered to this section in this academic session");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        validateOfferingConsistency(subject, section, faculty);

        SubjectOffering offering = SubjectOffering.builder()
                .subject(subject)
                .section(section)
                .faculty(faculty)
                .academicSession(request.getAcademicSession())
                .active(true)
                .build();

        SubjectOffering saved = subjectOfferingRepository.save(offering);
        return mapToResponse(saved);
    }

    @Transactional
    public SubjectOfferingResponse update(Long id, SubjectOfferingRequest request) {
        SubjectOffering offering = findOfferingById(id);

        boolean duplicateExists = subjectOfferingRepository
                .existsBySubjectIdAndSectionIdAndAcademicSession(
                        request.getSubjectId(), request.getSectionId(), request.getAcademicSession())
                && !(offering.getSubject().getId().equals(request.getSubjectId())
                && offering.getSection().getId().equals(request.getSectionId())
                && offering.getAcademicSession().equals(request.getAcademicSession()));

        if (duplicateExists) {
            throw new RuntimeException(
                    "This subject is already offered to this section in this academic session");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        validateOfferingConsistency(subject, section, faculty);

        offering.setSubject(subject);
        offering.setSection(section);
        offering.setFaculty(faculty);
        offering.setAcademicSession(request.getAcademicSession());

        return mapToResponse(subjectOfferingRepository.save(offering));
    }

    public List<SubjectOfferingResponse> getAll() {
        return subjectOfferingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SubjectOfferingResponse> getByFaculty(Long facultyId) {
        return subjectOfferingRepository.findByFacultyId(facultyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SubjectOfferingResponse> getMyOfferings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Faculty faculty = facultyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found for the logged-in user"));

        return getByFaculty(faculty.getId());
    }

    public List<SubjectOfferingResponse> getBySection(Long sectionId) {
        return subjectOfferingRepository.findBySectionId(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SubjectOfferingResponse> getMyDepartmentSubjectOfferings() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Faculty faculty = facultyRepository.findByUser_Email(email);

        if (faculty == null) {
            throw new AccessDeniedException("Faculty not found.");
        }

        if (faculty.getDesignation() != Designation.HOD) {
            throw new AccessDeniedException(
                    "Only HOD can access department subject offerings.");
        }

        Long departmentId = faculty.getDepartment().getId();

        return subjectOfferingRepository
                .findBySection_Semester_Batch_Program_Department_IdOrderBySubject_CodeAsc(
                        departmentId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SubjectOfferingResponse getById(Long id) {
        return mapToResponse(findOfferingById(id));
    }

    @Transactional
    public void deactivate(Long id) {
        SubjectOffering offering = findOfferingById(id);
        offering.setActive(false);
        subjectOfferingRepository.save(offering);
    }

    @Transactional
    public void delete(Long id) {
        subjectOfferingRepository.delete(findOfferingById(id));
    }

    private SubjectOffering findOfferingById(Long id) {
        return subjectOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject offering not found with id: " + id));
    }

    private SubjectOfferingResponse mapToResponse(SubjectOffering offering) {
        return SubjectOfferingResponse.builder()
                .id(offering.getId())
                .subjectId(offering.getSubject().getId())
                .subjectName(offering.getSubject().getName())
                .subjectCode(offering.getSubject().getCode())
                .sectionId(offering.getSection().getId())
                .sectionName(offering.getSection().getName())
                .programId(offering.getSubject().getProgram().getId())
                .programName(offering.getSubject().getProgram().getCode())
                .semesterNumber(offering.getSection().getSemester().getSemesterNumber())
                .facultyId(offering.getFaculty().getId())
                .facultyName(offering.getFaculty().getFullName())
                .academicSession(offering.getAcademicSession())
                .batchName(offering.getSection().getSemester().getBatch().getName())
                .departmentCode(offering.getSection().getSemester().getBatch().getProgram().getDepartment().getCode())
                .active(offering.isActive())
                .build();
    }

    private void validateOfferingConsistency(Subject subject, Section section, Faculty faculty) {
        Long sectionProgramId = section.getSemester().getBatch().getProgram().getId();
        if (!subject.getProgram().getId().equals(sectionProgramId)) {
            throw new RuntimeException(
                    "Subject does not belong to the program of the selected section");
        }

        if (!subject.getSemesterNumber().equals(section.getSemester().getSemesterNumber())) {
            throw new RuntimeException(
                    "Subject's semester does not match the selected section's semester");
        }

        if (faculty.getStatus() != com.kshitij.collegeerp.models.faculty.entity.FacultyStatus.ACTIVE) {
            throw new RuntimeException("Cannot assign an inactive faculty to a subject offering");
        }
    }
}
