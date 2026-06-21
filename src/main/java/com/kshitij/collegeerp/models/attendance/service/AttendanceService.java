package com.kshitij.collegeerp.models.attendance.service;

import com.kshitij.collegeerp.models.attendance.dto.AttendancePercentageResponse;
import com.kshitij.collegeerp.models.attendance.dto.AttendanceSessionResponse;
import com.kshitij.collegeerp.models.attendance.dto.MarkAttendanceRequest;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceRecord;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceSession;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceRecordRepository;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceSessionRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public AttendanceSessionResponse markAttendance(MarkAttendanceRequest request) {

        SubjectOffering offering = subjectOfferingRepository
                .findById(request.getSubjectOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject offering not found"));

        // Validation: Only assigned faculty can mark attendance
        String loggedInEmail = getCurrentUserEmail();
        if (!offering.getFaculty().getEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new RuntimeException("You are not authorized to mark attendance for this subject");
        }

        // Validation: duplicate session check
        boolean sessionExists = sessionRepository
                .findBySubjectOfferingIdAndSessionDateAndStartTime(
                        request.getSubjectOfferingId(),
                        request.getSessionDate(),
                        request.getStartTime())
                .isPresent();

        if (sessionExists) {
            throw new RuntimeException(
                    "Attendance for this subject, date, and time slot is already marked");
        }

        AttendanceSession session = AttendanceSession.builder()
                .subjectOffering(offering)
                .sessionDate(request.getSessionDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .submitted(true)
                .build();

        AttendanceSession savedSession = sessionRepository.save(session);

        // Validation: only allowed to the particular section students
        Long sectionId = offering.getSection().getId();

        for (MarkAttendanceRequest.StudentAttendanceEntry entry : request.getEntries()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student not found with id: " + entry.getStudentId()));

            if (!student.getSection().getId().equals(sectionId)) {
                throw new RuntimeException(
                        "Student " + student.getFullName() + " does not belong to this section");
            }

            AttendanceRecord record = AttendanceRecord.builder()
                    .session(savedSession)
                    .student(student)
                    .status(entry.getStatus())
                    .build();

            recordRepository.save(record);
        }

        return mapSessionToResponse(savedSession);
    }

    public AttendanceSessionResponse getSessionById(Long sessionId) {
        AttendanceSession session = findSessionById(sessionId);
        return mapSessionToResponse(session);
    }

    public List<AttendanceSessionResponse> getSessionsByOffering(Long subjectOfferingId) {
        return sessionRepository.findBySubjectOfferingId(subjectOfferingId)
                .stream()
                .map(this::mapSessionToResponse)
                .toList();
    }

    public List<AttendanceRecord> getStudentAttendanceHistory(Long studentId) {
        return recordRepository.findByStudentId(studentId);
    }

    public AttendancePercentageResponse getStudentPercentage(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        long total = recordRepository.countByStudentId(studentId);
        long present = recordRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT)
                + recordRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);

        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        return AttendancePercentageResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .totalClasses(total)
                .presentCount(present)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    private AttendanceSession findSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance session not found with id: " + id));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return authentication.getName();
    }

    private AttendanceSessionResponse mapSessionToResponse(AttendanceSession session) {
        List<AttendanceRecord> records = recordRepository.findBySessionId(session.getId());

        List<AttendanceSessionResponse.StudentRecordResponse> recordResponses = records.stream()
                .map(r -> AttendanceSessionResponse.StudentRecordResponse.builder()
                        .studentId(r.getStudent().getId())
                        .studentName(r.getStudent().getFullName())
                        .enrollmentNumber(r.getStudent().getEnrollmentNumber())
                        .status(r.getStatus().name())
                        .build())
                .toList();

        return AttendanceSessionResponse.builder()
                .id(session.getId())
                .subjectOfferingId(session.getSubjectOffering().getId())
                .subjectName(session.getSubjectOffering().getSubject().getName())
                .sectionName(session.getSubjectOffering().getSection().getName())
                .sessionDate(session.getSessionDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .submitted(session.isSubmitted())
                .records(recordResponses)
                .build();
    }
}