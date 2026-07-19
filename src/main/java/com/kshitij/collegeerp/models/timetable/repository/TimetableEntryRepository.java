package com.kshitij.collegeerp.models.timetable.repository;

import com.kshitij.collegeerp.models.timetable.entity.DayOfWeek;
import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long>,
        JpaSpecificationExecutor<TimetableEntry> {

    // TimeTable of section
    List<TimetableEntry>
    findBySubjectOfferingSectionIdAndAcademicSessionOrderByDayOfWeekAscTimeSlotStartTimeAsc(
            Long sectionId,
            String academicSession
    );

    // Faculty's TimeTable
    List<TimetableEntry>
    findBySubjectOfferingFacultyIdAndAcademicSessionOrderByDayOfWeekAscTimeSlotStartTimeAsc(
            Long facultyId,
            String academicSession
    );

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

    boolean existsByRoomId(Long roomId);
    boolean existsByTimeSlotId(Long timeSlotId);
    List<TimetableEntry> findAllByOrderByDayOfWeekAscTimeSlotStartTimeAsc();

    boolean existsByDayOfWeekAndTimeSlotIdAndRoomIdAndAcademicSessionAndIdNot(
            DayOfWeek dayOfWeek,
            Long timeSlotId,
            Long roomId,
            String academicSession,
            Long id
    );

    boolean existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingFacultyIdAndAcademicSessionAndIdNot(
            DayOfWeek dayOfWeek,
            Long timeSlotId,
            Long facultyId,
            String academicSession,
            Long id
    );

    boolean existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingSectionIdAndAcademicSessionAndIdNot(
            DayOfWeek dayOfWeek,
            Long timeSlotId,
            Long sectionId,
            String academicSession,
            Long id
    );

    List<TimetableEntry> findBySubjectOfferingFacultyEmailAndDayOfWeekOrderByTimeSlotStartTime(
            String email,
            DayOfWeek dayOfWeek
    );
    List<TimetableEntry>
    findByDayOfWeekOrderByTimeSlotStartTime(DayOfWeek day);

    List<TimetableEntry>
    findBySubjectOffering_Section_Semester_Batch_Program_Department_IdAndDayOfWeekOrderByTimeSlot_StartTime(
            Long departmentId,
            DayOfWeek day
    );


}