package com.kshitij.collegeerp.models.exam.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.exam.dto.MarksEntryRequest;
import com.kshitij.collegeerp.models.exam.dto.ResultResponse;
import com.kshitij.collegeerp.models.exam.entity.Exam;
import com.kshitij.collegeerp.models.exam.entity.ExamType;
import com.kshitij.collegeerp.models.exam.entity.Marks;
import com.kshitij.collegeerp.models.exam.repository.MarksRepository;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final ExamService examService;

    @Transactional
    public List<ResultResponse> enterMarks(MarksEntryRequest request) {
        Exam exam = examService.findExamById(request.getExamId());

        if(exam.isLocked()){
            throw new RuntimeException("Cannot enter marks - exam is locked");
        }

        // validation: Marks of external examination - only admin can enter
        if(exam.getExamType() == ExamType.EXTERNAL && !isAdmin()) {
            throw new RuntimeException("Only Admin/Exam Cell can enter marks for external exams");
        }

        // Validation: Only assigned faculty can enter marks for class test or internal exams
        if(exam.getExamType() != ExamType.EXTERNAL && !isAdmin()) {
            String loggedInEmail = getCurrentUserEmail();
            String assignedFacultyEmail = exam.getSubjectOffering().getFaculty().getEmail();
            if(!assignedFacultyEmail.equalsIgnoreCase(loggedInEmail)) {
                throw new RuntimeException("You are not authorized for enter the marks for this subject");
            }
        }

        Long sectionId = exam.getSubjectOffering().getSection().getId();

        for(MarksEntryRequest.StudentMarksEntry entry : request.getEntries()) {
            if(entry.getMarksObtained() > exam.getMaxMarks()) {
                throw new RuntimeException("Marks obtained cannot exceed max marks (" + exam.getMaxMarks() + ")");
            }

            if(entry.getMarksObtained() < 0) {
                throw new RuntimeException("Marks obtained cannot be negative");
            }

            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + entry.getStudentId()));

            if(!student.getSection().getId().equals(sectionId)) {
                throw new RuntimeException(
                        "Student " + student.getFullName() + " does not belong to this section"
                );
            }

            Marks marks = marksRepository.findByExamIdAndStudentId(exam.getId(), student.getId())
                    .orElse(null);

            if(marks == null) {
                marks = Marks.builder()
                        .exam(exam)
                        .student(student)
                        .marksObtained(entry.getMarksObtained())
                        .build();
            } else {
                marks.setMarksObtained(entry.getMarksObtained());
            }
            marksRepository.save(marks);
        }
        return getResultsByExam(exam.getId());
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    public List<ResultResponse> getResultsByExam(Long id) {
        Exam exam = examService.findExamById(id);
        return marksRepository.findByExamId(id)
                .stream()
                .map(m -> mapToResultResponse(m, exam))
                .toList();
    }

    private ResultResponse mapToResultResponse(Marks m, Exam exam) {
        return ResultResponse.builder()
                .studentId(m.getStudent().getId())
                .studentName(m.getStudent().getFullName())
                .enrollmentNumber(m.getStudent().getEnrollmentNumber())
                .marksObtained(m.getMarksObtained())
                .maxMarks(exam.getMaxMarks())
                .examName(exam.getName())
                .build();
        
    }

    public List<ResultResponse> getResultsByStudent(Long studentId, boolean onlyPublished) {
        return marksRepository.findByStudentId(studentId)
                .stream()
                .filter(m -> !onlyPublished || m.getExam().isResultPublished())
                .map(m -> mapToResultResponse(m, m.getExam()))
                .toList();
    }
}
