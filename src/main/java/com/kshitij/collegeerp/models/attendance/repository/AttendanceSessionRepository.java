package com.kshitij.collegeerp.models.attendance.repository;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findBySubjectOfferingId(Long subjectOfferingId);
    Optional<AttendanceSession> findBySubjectOfferingIdAndSessionDateAndStartTime(
            Long subjectOfferingId, LocalDate sessionDate, LocalTime startTime);
    Optional<AttendanceSession> findByTimetableEntryIdAndSessionDate(
            Long timetableEntryId,
            LocalDate sessionDate
    );

    List<AttendanceSession> findAllByOrderBySessionDateDescStartTimeDesc();
    List<AttendanceSession>
    findBySubjectOfferingFacultyEmailOrderBySessionDateDescStartTimeDesc(
            String email
    );

    List<AttendanceSession>
    findBySubjectOffering_Section_Semester_Batch_Program_Department_IdOrderBySessionDateDescStartTimeDesc(
            Long departmentId
    );
}
