package com.kshitij.collegeerp.models.attendance.service;

import com.kshitij.collegeerp.models.attendance.dto.*;
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
import com.kshitij.collegeerp.models.timetable.entity.DayOfWeek;
import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import com.kshitij.collegeerp.models.timetable.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final StudentRepository studentRepository;
    private final TimetableEntryRepository timetableEntryRepository;

    @Transactional
    public AttendanceSessionResponse markAttendance(MarkAttendanceRequest request) {

        // ==========================================
        // Validate Date
        // ==========================================

        if (request.getSessionDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(
                    "Attendance cannot be marked for a future date.");
        }



        // ==========================================
        // Fetch Subject Offering
        // ==========================================

        TimetableEntry timetableEntry =
                timetableEntryRepository.findById(
                                request.getTimetableEntryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Timetable entry not found"));
        SubjectOffering offering =
                timetableEntry.getSubjectOffering();

        // ==========================================
        // Validate Time
        // ==========================================

        if (!timetableEntry.getTimeSlot().getEndTime().isAfter(timetableEntry.getTimeSlot().getStartTime())) {
            throw new RuntimeException(
                    "End time must be after start time.");
        }

        // ==========================================
        // Validate Faculty
        // ==========================================

        validateFacultyAccess(offering);

        // ==========================================
        // Prevent Duplicate Attendance Session
        // ==========================================

        boolean sessionExists = sessionRepository
                .findByTimetableEntryIdAndSessionDate(
                        request.getTimetableEntryId(),
                        request.getSessionDate()
                )
                .isPresent();

        if (sessionExists) {
            throw new RuntimeException(
                    "Attendance has already been marked for this session.");
        }

        // ==========================================
        // Validate Student Count
        // ==========================================

        Long sectionId = offering.getSection().getId();

        long totalStudents = studentRepository.countBySectionId(sectionId);

        if (request.getEntries().size() != totalStudents) {
            throw new RuntimeException(
                    "Attendance must be marked for all students in the section.");
        }

        // ==========================================
        // Validate Duplicate Students
        // ==========================================

        Set<Long> studentIds = new HashSet<>();

        for (MarkAttendanceRequest.StudentAttendanceEntry entry : request.getEntries()) {

            if (!studentIds.add(entry.getStudentId())) {
                throw new RuntimeException(
                        "Duplicate student found in attendance request.");
            }
        }

        // ==========================================
        // Create Attendance Session
        // ==========================================

        AttendanceSession session = AttendanceSession.builder()
                .timetableEntry(timetableEntry)
                .subjectOffering(offering)
                .sessionDate(request.getSessionDate())
                .startTime(timetableEntry.getTimeSlot().getStartTime())
                .endTime(timetableEntry.getTimeSlot().getEndTime())
                .submitted(true)
                .build();

        AttendanceSession savedSession = sessionRepository.save(session);

        // ==========================================
        // Save Attendance Records
        // ==========================================

        for (MarkAttendanceRequest.StudentAttendanceEntry entry : request.getEntries()) {

            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Student not found with id: " + entry.getStudentId()));

            if (!student.getSection().getId().equals(sectionId)) {
                throw new RuntimeException(
                        "Student " + student.getFullName()
                                + " does not belong to the selected section.");
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

    public List<FacultyClassResponse> getMyClasses(LocalDate date) {
        String email = getCurrentUserEmail();

        java.time.DayOfWeek javaDay = date.getDayOfWeek();

        com.kshitij.collegeerp.models.timetable.entity.DayOfWeek day =
                com.kshitij.collegeerp.models.timetable.entity.DayOfWeek.valueOf(javaDay.name());

        List<TimetableEntry> classes =
                timetableEntryRepository
                        .findBySubjectOfferingFacultyEmailAndDayOfWeekOrderByTimeSlotStartTime(
                                email,
                                day
                        );

        return classes.stream()
                .map(this::mapToFacultyClassResponse)
                .toList();
    }

    public List<AttendanceStudentResponse> getStudentsForClass(
            Long timetableEntryId
    ) {

        // ==========================================
        // Fetch Timetable Entry
        // ==========================================

        TimetableEntry timetableEntry = timetableEntryRepository
                .findById(timetableEntryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Timetable entry not found with id: " + timetableEntryId));

        SubjectOffering offering = timetableEntry.getSubjectOffering();

        // ==========================================
        // Authorization
        // ==========================================

        validateFacultyAccess(offering);

        // ==========================================
        // Fetch Students
        // ==========================================

        Long sectionId = offering.getSection().getId();

        List<Student> students = studentRepository
                .findBySectionIdOrderByRegistrationNumber(sectionId);

        // ==========================================
        // Map Response
        // ==========================================

        return students.stream()
                .map(student -> AttendanceStudentResponse.builder()
                        .studentId(student.getId())
                        .registrationNumber(student.getRegistrationNumber())
                        .fullName(student.getFullName())
                        .build())
                .toList();
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

    private void validateFacultyAccess(SubjectOffering offering) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String loggedInEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isAssignedFaculty = offering.getFaculty()
                .getEmail()
                .equalsIgnoreCase(loggedInEmail);

        if (!isAdmin && !isAssignedFaculty) {
            throw new RuntimeException(
                    "You are not authorized to perform this action.");
        }
    }

    private FacultyClassResponse mapToFacultyClassResponse(
            TimetableEntry entry
    ) {

        return FacultyClassResponse.builder()

                .timetableEntryId(entry.getId())

                .subjectOfferingId(entry.getSubjectOffering().getId())

                .subjectName(
                        entry.getSubjectOffering()
                                .getSubject()
                                .getName())

                .subjectCode(
                        entry.getSubjectOffering()
                                .getSubject()
                                .getCode())

                .sectionName(
                        entry.getSubjectOffering()
                                .getSection()
                                .getName())

                .roomNumber(
                        entry.getRoom()
                                .getRoomNumber())

                .startTime(
                        entry.getTimeSlot()
                                .getStartTime())

                .endTime(
                        entry.getTimeSlot()
                                .getEndTime())

                .build();
    }
}