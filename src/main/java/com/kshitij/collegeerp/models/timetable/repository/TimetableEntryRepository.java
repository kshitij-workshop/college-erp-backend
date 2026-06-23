package com.kshitij.collegeerp.models.timetable.repository;

import com.kshitij.collegeerp.models.timetable.entity.DayOfWeek;
import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    // TimeTable of section
    List<TimetableEntry> findBySubjectOfferingSectionIdAndAcademicSession(
            Long sectionId, String academicSession);

    // Faculty's TimeTable
    List<TimetableEntry> findBySubjectOfferingFacultyIdAndAcademicSession(
            Long facultyId, String academicSession);

    // Room conflict check
    boolean existsByDayOfWeekAndTimeSlotIdAndRoomIdAndAcademicSession(
            DayOfWeek dayOfWeek, Long timeSlotId,
            Long roomId, String academicSession);

    // Faculty conflict check
    boolean existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingFacultyIdAndAcademicSession(
            DayOfWeek dayOfWeek, Long timeSlotId,
            Long facultyId, String academicSession);

    // Section conflict check
    boolean existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingSectionIdAndAcademicSession(
            DayOfWeek dayOfWeek, Long timeSlotId,
            Long sectionId, String academicSession);
}