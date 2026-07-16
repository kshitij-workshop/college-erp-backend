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
import com.kshitij.collegeerp.models.subject.entity.Subject;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import com.kshitij.collegeerp.models.timetable.repository.TimetableEntryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<FacultyClassResponse> getClasses(LocalDate date) {

        java.time.DayOfWeek javaDay = date.getDayOfWeek();

        com.kshitij.collegeerp.models.timetable.entity.DayOfWeek day =
                com.kshitij.collegeerp.models.timetable.entity.DayOfWeek.valueOf(javaDay.name());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        List<TimetableEntry> classes = isAdmin ?
                timetableEntryRepository
                        .findByDayOfWeekOrderByTimeSlotStartTime(day) :

                timetableEntryRepository
                        .findBySubjectOfferingFacultyEmailAndDayOfWeekOrderByTimeSlotStartTime(
                                email,
                                day
                        );

        return classes.stream()
                .map(entry -> mapToFacultyClassResponse(entry, date))
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
                .map(student -> mapStudent(
                        student,
                        AttendanceStatus.PRESENT
                ))
                .toList();
    }


    public List<AttendanceSessionResponse> getSessionsByOffering(Long subjectOfferingId) {
        return sessionRepository.findBySubjectOfferingId(subjectOfferingId)
                .stream()
                .map(this::mapSessionToResponse)
                .toList();
    }

    public List<AttendanceStudentHistoryResponse> getStudentAttendanceHistory(Long studentId) {
        List<AttendanceRecord> records = recordRepository.findByStudentId(studentId);

        return records.stream()
                .map(this::mapAttendanceStudentHistory)
                .toList();
    }


    private AttendanceStudentHistoryResponse mapAttendanceStudentHistory(AttendanceRecord record) {
        AttendanceSession session = record.getSession();
        Subject subject = session.getSubjectOffering().getSubject();

        return AttendanceStudentHistoryResponse.builder()
                .sessionId(session.getId())
                .sessionDate(session.getSessionDate())
                .subjectName(subject.getName())
                .subjectCode(subject.getCode())
                .sectionName(session.getSubjectOffering().getSection().getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(record.getStatus())
                .build();
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

    // Attendance History
    public List<AttendanceHistoryResponse> getAttendanceHistory() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        List<AttendanceSession> sessions;

        if (isAdmin) {
            sessions = sessionRepository
                    .findAllByOrderBySessionDateDescStartTimeDesc();
        } else {
            sessions = sessionRepository
                    .findBySubjectOfferingFacultyEmailOrderBySessionDateDescStartTimeDesc(
                            email
                    );
        }

        return sessions.stream()
                .map(session -> {
                    List<AttendanceRecord> records =
                            recordRepository
                                    .findBySessionId(session.getId());
                    return mapAttendanceHistory(session, records);
                })
                .toList();
    }

    public AttendanceSessionDetailsResponse getAttendanceSession(Long sessionId) {
        AttendanceSession session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"));

        validateFacultyAccess(session.getSubjectOffering());

        List<AttendanceRecord> records = recordRepository.findBySessionId(sessionId);

        List<AttendanceStudentResponse> students = records.stream()
                .sorted(Comparator.comparing(record -> record.getStudent().getRegistrationNumber()))
                        .map(record ->
                                AttendanceStudentResponse.builder()
                                        .studentId(
                                                record.getStudent().getId())
                                        .rollNumber(
                                                record.getStudent().getRollNumber())
                                        .registrationNumber(
                                                record.getStudent().getRegistrationNumber())
                                        .fullName(
                                                record.getStudent().getFullName())
                                        .status(
                                                record.getStatus())
                                        .build()
                        )
                        .toList();

        return AttendanceSessionDetailsResponse.builder()

                .sessionId(session.getId())

                .sessionDate(session.getSessionDate())

                .startTime(session.getStartTime())

                .endTime(session.getEndTime())

                .subjectName(
                        session.getSubjectOffering()
                                .getSubject()
                                .getName())

                .subjectCode(
                        session.getSubjectOffering()
                                .getSubject()
                                .getCode())

                .sectionName(
                        session.getSubjectOffering()
                                .getSection()
                                .getName())

                .students(students)

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

    private AttendanceHistoryResponse mapAttendanceHistory(
            AttendanceSession session,
            List<AttendanceRecord> records
    ) {

        long present = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT)
                .count();

        long absent = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.ABSENT)
                .count();

        long late = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.LATE)
                .count();

        long leave = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.LEAVE)
                .count();

        return AttendanceHistoryResponse.builder()
                .sessionId(session.getId())
                .sessionDate(session.getSessionDate())
                .subjectName(
                        session.getSubjectOffering()
                                .getSubject()
                                .getName())
                .subjectCode(
                        session.getSubjectOffering()
                                .getSubject()
                                .getCode())
                .sectionName(
                        session.getSubjectOffering()
                                .getSection()
                                .getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .leaveCount(leave)
                .build();
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


    private void validateAttendanceEditAccess(
            AttendanceSession session
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        // Admin can always edit
        if (isAdmin) {
            return;
        }

        // Verify assigned faculty
        if (!session.getSubjectOffering()
                .getFaculty()
                .getEmail()
                .equalsIgnoreCase(email)) {

            throw new RuntimeException(
                    "You are not authorized to update this attendance.");
        }
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
            TimetableEntry entry, LocalDate date
    ) {

        boolean marked = sessionRepository
                .findByTimetableEntryIdAndSessionDate(
                        entry.getId(),
                        date
                )
                .isPresent();

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
                                .getCode()
                                )

                .facultyName(
                        entry.getSubjectOffering()
                                .getFaculty().getFullName()
                )

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

                .attendanceMarked(marked)


                .build();
    }

    public AttendanceSheetResponse getAttendanceSheet(
            Long timetableEntryId,
            LocalDate date
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        TimetableEntry timetableEntry = timetableEntryRepository
                .findById(timetableEntryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Timetable entry not found."));

        SubjectOffering offering = timetableEntry.getSubjectOffering();

        validateFacultyAccess(offering);

        Optional<AttendanceSession> existingSession =
                sessionRepository
                        .findByTimetableEntryIdAndSessionDate(
                                timetableEntryId,
                                date
                        );

        if (existingSession.isPresent()) {

            AttendanceSession session = existingSession.get();



            List<AttendanceRecord> records =
                    recordRepository
                            .findBySessionId(session.getId());

            List<AttendanceStudentResponse> students =
                    records.stream()
                            .sorted(Comparator.comparing(
                                    record -> record.getStudent().getRegistrationNumber()
                            ))
                            .map(record ->
                                    mapStudent(
                                            record.getStudent(),
                                            record.getStatus()
                                    )
                            )
                            .toList();

            return AttendanceSheetResponse.builder()
                    .attendanceMarked(true)
                    .sessionId(session.getId())
                    .timetableEntryId(timetableEntryId)
                    .subjectName(offering.getSubject().getName())
                    .sectionName(offering.getSection().getName())
                    .sessionDate(date)
                    .students(students)
                    .build();
        }

        List<Student> students =
                studentRepository.findBySectionIdOrderByRegistrationNumber(
                        offering.getSection().getId()
                );

        List<AttendanceStudentResponse> attendanceStudents =
                students.stream()
                        .map(student ->
                                mapStudent(
                                        student,
                                        AttendanceStatus.PRESENT
                                )
                        )
                        .toList();

        return AttendanceSheetResponse.builder()
                .attendanceMarked(false)
                .sessionId(null)
                .timetableEntryId(timetableEntryId)
                .subjectName(offering.getSubject().getName())
                .sectionName(offering.getSection().getName())
                .sessionDate(date)
                .students(attendanceStudents)
                .build();
    }

    public StudentAttendanceDashboardResponse getStudentDashboard(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);

        List<AttendanceRecord> records =
                recordRepository.findByStudentId(student.getId());

        int totalClasses = records.size();

        int presentClasses = (int) records.stream()
                .filter(r ->
                        r.getStatus() == AttendanceStatus.PRESENT ||
                                r.getStatus() == AttendanceStatus.LATE
                )
                .count();

        double overallPercentage = totalClasses == 0
                ? 0
                : (presentClasses * 100.0) / totalClasses;

        Map<SubjectOffering, List<AttendanceRecord>> grouped =
                records.stream()
                        .collect(Collectors.groupingBy(
                                record -> record.getSession().getSubjectOffering()
                        ));

        List<StudentSubjectAttendanceResponse> subjects = new ArrayList<>();

        for (Map.Entry<SubjectOffering, List<AttendanceRecord>> entry : grouped.entrySet()) {

            SubjectOffering offering = entry.getKey();

            List<AttendanceRecord> attendance = entry.getValue();

            int total = attendance.size();

            int present = (int) attendance.stream()
                    .filter(r ->
                            r.getStatus() == AttendanceStatus.PRESENT ||
                                    r.getStatus() == AttendanceStatus.LATE
                    )
                    .count();

            double percentage = total == 0
                    ? 0
                    : (present * 100.0) / total;

            List<StudentAttendanceHistoryItemResponse> history =
                    attendance.stream()
                            .sorted(
                                    Comparator.comparing(
                                            (AttendanceRecord record) -> record.getSession().getSessionDate()
                                    ).reversed()
                            )
                            .map(record -> StudentAttendanceHistoryItemResponse.builder()
                                    .attendanceRecordId(record.getId())
                                    .attendanceDate(record.getSession().getSessionDate())
                                    .status(record.getStatus())
                                    .build())
                            .toList();

            subjects.add(
                    StudentSubjectAttendanceResponse.builder()
                            .subjectOfferingId(offering.getId())
                            .subjectCode(offering.getSubject().getCode())
                            .subjectName(offering.getSubject().getName())
                            .facultyName(offering.getFaculty().getFullName())
                            .presentClasses(present)
                            .totalClasses(total)
                            .percentage(percentage)
                            .attendanceHistory(history)
                            .build()
            );
        }

        return StudentAttendanceDashboardResponse.builder()
                .overallPercentage(overallPercentage)
                .presentClasses(presentClasses)
                .totalClasses(totalClasses)
                .subjects(subjects)
                .build();
    }

    private AttendanceStudentResponse mapStudent(
            Student student,
            AttendanceStatus status
    ) {

        return AttendanceStudentResponse.builder()
                .studentId(student.getId())
                .rollNumber(student.getRollNumber())
                .registrationNumber(student.getRegistrationNumber())
                .fullName(student.getFullName())
                .status(status)
                .build();
    }

    @Transactional
    public AttendanceSessionResponse updateAttendance(Long sessionId, @Valid UpdateAttendanceRequest request) {
        AttendanceSession session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found."));

        validateFacultyAccess(session.getSubjectOffering());

        List<AttendanceRecord> records =
                recordRepository.findBySessionId(sessionId);

        Map<Long, AttendanceRecord> recordMap =
                records.stream()
                        .collect(Collectors.toMap(
                                record -> record.getStudent().getId(),
                                Function.identity()
                        ));

        for (UpdateAttendanceRequest.StudentAttendanceEntry entry : request.getEntries()) {

            AttendanceRecord record = recordMap.get(entry.getStudentId());

            if (record == null) {
                throw new ResourceNotFoundException(
                        "Attendance record not found for student id: "
                                + entry.getStudentId());
            }

            record.setStatus(entry.getStatus());
        }

        recordRepository.saveAll(records);
        System.out.println("Saved: " + records.size());

        return mapSessionToResponse(session);
    }
}