package com.kshitij.collegeerp.models.timetable.service;

import com.kshitij.collegeerp.common.exception.BadRequestException;
import com.kshitij.collegeerp.common.exception.ConflictException;
import com.kshitij.collegeerp.common.exception.DuplicateResourceException;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.timetable.dto.TimeSlotRequest;
import com.kshitij.collegeerp.models.timetable.dto.TimeSlotResponse;
import com.kshitij.collegeerp.models.timetable.entity.TimeSlot;
import com.kshitij.collegeerp.models.timetable.repository.TimeSlotRepository;
import com.kshitij.collegeerp.models.timetable.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimetableEntryRepository timetableEntryRepository;

    // =========================================================
    // Create Time Slot
    // =========================================================

    @Transactional
    public TimeSlotResponse create(TimeSlotRequest request) {

        validateTime(request);

        TimeSlot slot = TimeSlot.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .label(request.getLabel().trim())
                .build();

        return mapToResponse(
                timeSlotRepository.save(slot)
        );
    }

    // =========================================================
    // Update Time Slot
    // =========================================================

    @Transactional
    public TimeSlotResponse update(
            Long id,
            TimeSlotRequest request
    ) {

        TimeSlot slot = timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Time slot not found with id: " + id));

        validateTime(id, request);

        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setLabel(request.getLabel().trim());

        return mapToResponse(
                timeSlotRepository.save(slot)
        );
    }

    // =========================================================
    // Get By Id
    // =========================================================

    public TimeSlotResponse getById(Long id) {

        TimeSlot slot = timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Time slot not found with id: " + id));

        return mapToResponse(slot);
    }

    // =========================================================
    // Get All
    // =========================================================

    public List<TimeSlotResponse> getAll() {

        return timeSlotRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // Delete Time Slot
    // =========================================================

    @Transactional
    public void delete(Long id) {

        TimeSlot slot = timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Time slot not found with id: " + id));

        if (timetableEntryRepository.existsByTimeSlotId(id)) {
            throw new RuntimeException(
                    "Cannot delete time slot because it is assigned to the timetable."
            );
        }

        timeSlotRepository.delete(slot);
    }

    // =========================================================
    // Validation
    // =========================================================

    private void validateTime(TimeSlotRequest request) {

        if (timeSlotRepository.existsOverlappingSlot(
                request.getStartTime(),
                request.getEndTime()
        )) {

            throw new ConflictException(
                    "Time slot overlaps with an existing time slot."
            );
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {

            throw new BadRequestException(
                    "End time must be after start time."
            );
        }
    }
    
    private void validateTime(
            Long id,
            TimeSlotRequest request
    ) {

        if (timeSlotRepository.existsOverlappingSlot(
                id,
                request.getStartTime(),
                request.getEndTime()
        )) {

            throw new ConflictException(
                    "Time slot overlaps with an existing time slot."
            );
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {

            throw new BadRequestException(
                    "End time must be after start time."
            );
        }
    }

    // =========================================================
    // Mapper
    // =========================================================

    private TimeSlotResponse mapToResponse(TimeSlot slot) {

        return TimeSlotResponse.builder()
                .id(slot.getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .label(slot.getLabel())
                .build();
    }

}