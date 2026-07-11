package com.kshitij.collegeerp.models.timetable.repository;

import com.kshitij.collegeerp.models.timetable.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    boolean existsByStartTimeAndEndTime(
            LocalTime startTime,
            LocalTime endTime
    );
}
