package com.kshitij.collegeerp.models.assignment.service;

import com.kshitij.collegeerp.models.assignment.dto.*;
import com.kshitij.collegeerp.models.assignment.entity.*;
import com.kshitij.collegeerp.models.assignment.repository.*;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final StudentRepository studentRepository;

    // ─── Assignment CRUD ──────────────────────────────────────

    @Transactional
    public AssignmentResponse create(AssignmentRequest request) {
        SubjectOffering offering = subjectOfferingRepository
                .findById(request.getSubjectOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject offering not found"));

        // Validation: Only assigned faculty can create assignment
        String loggedInEmail = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();
        if (!offering.getFaculty().getEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new RuntimeException(
                    "You are not authorized to create assignment for this subject");
        }

        // Validation: due date must be in future
        if (request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Due date must be in the future");
        }

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subjectOffering(offering)
                .dueDate(request.getDueDate())
                .maxMarks(request.getMaxMarks())
                .active(true)
                .build();

        return mapToResponse(assignmentRepository.save(assignment));
    }

    public List<AssignmentResponse> getBySubjectOffering(Long subjectOfferingId) {
        return assignmentRepository.findBySubjectOfferingId(subjectOfferingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AssignmentResponse> getBySection(Long sectionId) {
        return assignmentRepository.findBySubjectOfferingSectionId(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AssignmentResponse getById(Long id) {
        return mapToResponse(findAssignmentById(id));
    }

    public List<StudentAssignmentResponse> getMyAssignments() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);

        List<Assignment> assignments =
                assignmentRepository.findBySubjectOfferingSectionIdAndActiveTrue(
                        student.getSection().getId()
                );

        Map<Long, AssignmentSubmission> submissionMap =
                submissionRepository.findByStudentId(student.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                submission -> submission.getAssignment().getId(),
                                Function.identity()
                        ));

        return assignments.stream()
                .map(assignment ->
                        mapToStudentResponse(
                                assignment,
                                submissionMap.get(assignment.getId())
                        )
                )
                .toList();
    }

    public List<SubmissionResponse> getMySubmissions() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);

        return submissionRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }

    public SubmissionResponse getMySubmission(Long assignmentId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);

        AssignmentSubmission submission =
                submissionRepository
                        .findByAssignmentIdAndStudentId(
                                assignmentId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        return mapSubmissionToResponse(submission);
    }

    @Transactional
    public AssignmentResponse update(Long id, AssignmentRequest request) {
        Assignment assignment = findAssignmentById(id);

        // Validation: due date must be in future
        if (request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Due date must be in the future");
        }

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxMarks(request.getMaxMarks());

        return mapToResponse(assignmentRepository.save(assignment));
    }

    @Transactional
    public void delete(Long id) {
        assignmentRepository.delete(findAssignmentById(id));
    }

    // ─── Submission ───────────────────────────────────────────

    @Transactional
    public SubmissionResponse submit(SubmissionRequest request) {
        Assignment assignment = findAssignmentById(request.getAssignmentId());

        // Get the currently authenticated student — never trust studentId from the request
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);

        // Validation: student must belong to the particular section
        Long sectionId = assignment.getSubjectOffering().getSection().getId();
        if (!student.getSection().getId().equals(sectionId)) {
            throw new RuntimeException(
                    "Student does not belong to this assignment's section");
        }

        boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());

        if (submissionRepository.existsByAssignmentIdAndStudentId(
                assignment.getId(), student.getId())) {
            if (isLate) {
                throw new RuntimeException("Cannot resubmit after due date");
            }
            AssignmentSubmission existing = submissionRepository
                    .findByAssignmentIdAndStudentId(
                            assignment.getId(), student.getId()).get();
            existing.setSubmissionText(request.getSubmissionText());
            existing.setSubmittedAt(LocalDateTime.now());
            existing.setStatus(SubmissionStatus.RESUBMITTED);

            existing.setMarksAwarded(null);
            existing.setFeedback(null);
           
            return mapSubmissionToResponse(submissionRepository.save(existing));
        }

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .student(student)
                .submissionText(request.getSubmissionText())
                .submittedAt(LocalDateTime.now())
                .late(isLate)
                .status(SubmissionStatus.SUBMITTED)
                .build();

        return mapSubmissionToResponse(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionResponse grade(Long submissionId, GradeRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + submissionId));

        // Validation: marks cannot be awarded more than max marks
        if (request.getMarksAwarded() > submission.getAssignment().getMaxMarks()) {
            throw new RuntimeException(
                    "Marks awarded cannot exceed max marks ("
                            + submission.getAssignment().getMaxMarks() + ")");
        }

        if (request.getMarksAwarded() < 0) {
            throw new RuntimeException("Marks cannot be negative");
        }

        submission.setMarksAwarded(request.getMarksAwarded());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);

        return mapSubmissionToResponse(submissionRepository.save(submission));
    }

    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }

    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }

    // ─── Private Helpers ──────────────────────────────────────

    private Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignment not found with id: " + id));
    }

    private AssignmentResponse mapToResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .subjectOfferingId(a.getSubjectOffering().getId())
                .subjectName(a.getSubjectOffering().getSubject().getName())
                .sectionName(a.getSubjectOffering().getSection().getName())
                .facultyName(a.getSubjectOffering().getFaculty().getFullName())
                .batchName(a.getSubjectOffering().getSection().getSemester().getBatch().getName())
                .departmentCode(a.getSubjectOffering().getSection().getSemester().getBatch().getProgram().getDepartment().getCode())
                .dueDate(a.getDueDate())
                .maxMarks(a.getMaxMarks())
                .active(a.isActive())
                .build();
    }

    private SubmissionResponse mapSubmissionToResponse(AssignmentSubmission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment().getId())
                .assignmentTitle(s.getAssignment().getTitle())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getFullName())
                .registrationNumber(s.getStudent().getRegistrationNumber())
                .submissionText(s.getSubmissionText())
                .submittedAt(s.getSubmittedAt())
                .late(s.isLate())
                .marksAwarded(s.getMarksAwarded())
                .feedback(s.getFeedback())
                .status(s.getStatus())
                .build();
    }

    private StudentAssignmentResponse mapToStudentResponse(
            Assignment assignment,
            AssignmentSubmission submission
    ) {

        return StudentAssignmentResponse.builder()

                .assignmentId(assignment.getId())

                .title(assignment.getTitle())

                .description(assignment.getDescription())

                .subjectName(
                        assignment.getSubjectOffering()
                                .getSubject()
                                .getName()
                )

                .subjectCode(
                        assignment.getSubjectOffering()
                                .getSubject()
                                .getCode()
                )

                .facultyName(
                        assignment.getSubjectOffering()
                                .getFaculty()
                                .getFullName()
                )

                .dueDate(assignment.getDueDate())

                .maxMarks(assignment.getMaxMarks())

                .submissionStatus(
                        submission == null
                                ? SubmissionStatus.PENDING
                                : submission.getStatus()
                )

                .submittedAt(
                        submission == null
                                ? null
                                : submission.getSubmittedAt()
                )

                .marksAwarded(
                        submission == null
                                ? null
                                : submission.getMarksAwarded()
                )

                .feedback(
                        submission == null
                                ? null
                                : submission.getFeedback()
                )

                .late(
                        submission != null
                                && submission.isLate()
                )

                .attachmentName(null)

                .attachmentUrl(null)

                .build();
    }}