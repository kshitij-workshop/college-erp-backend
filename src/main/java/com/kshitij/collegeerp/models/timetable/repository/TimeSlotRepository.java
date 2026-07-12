package com.kshitij.collegeerp.models.timetable.repository;

import com.kshitij.collegeerp.models.timetable.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    boolean existsByStartTimeAndEndTime(
            LocalTime startTime,
            LocalTime endTime
    );
    boolean existsByStartTimeAndEndTimeAndIdNot(
            LocalTime startTime,
            LocalTime endTime,
            Long id
    );

    @Query("""
SELECT COUNT(t) > 0
FROM TimeSlot t
WHERE t.startTime < :endTime
  AND t.endTime > :startTime
""")
    boolean existsOverlappingSlot(
            LocalTime startTime,
            LocalTime endTime
    );

    @Query("""
SELECT COUNT(t) > 0
FROM TimeSlot t
WHERE t.id <> :id
  AND t.startTime < :endTime
  AND t.endTime > :startTime
""")
    boolean existsOverlappingSlot(
            Long id,
            LocalTime startTime,
            LocalTime endTime
    );
}
