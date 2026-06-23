package com.kshitij.collegeerp.models.timetable.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import com.kshitij.collegeerp.models.timetable.dto.*;
import com.kshitij.collegeerp.models.timetable.entity.*;
import com.kshitij.collegeerp.models.timetable.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableEntryRepository timetableEntryRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;

    // ─── Room ────────────────────────────────────────────────

    @Transactional
    public RoomResponse addRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("Room already exists: " + request.getRoomNumber());
        }
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .capacity(request.getCapacity())
                .roomType(request.getRoomType())
                .active(true)
                .build();
        return mapRoomToResponse(roomRepository.save(room));
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapRoomToResponse).toList();
    }

    // ─── TimeSlot ─────────────────────────────────────────────

    @Transactional
    public TimeSlotResponse addTimeSlot(TimeSlotRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().equals(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }
        TimeSlot slot = TimeSlot.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .label(request.getLabel())
                .build();
        return mapSlotToResponse(timeSlotRepository.save(slot));
    }

    public List<TimeSlotResponse> getAllTimeSlots() {
        return timeSlotRepository.findAll().stream()
                .map(this::mapSlotToResponse).toList();
    }

    // ─── Timetable Entry ──────────────────────────────────────

    @Transactional
    public TimetableEntryResponse createEntry(TimetableEntryRequest request) {

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        SubjectOffering offering = subjectOfferingRepository
                .findById(request.getSubjectOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject offering not found"));

        // ─── Conflict Checks ──────────────────────────────────

        // 1. Room conflict — same room, same day, same time
        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndRoomIdAndAcademicSession(
                        request.getDayOfWeek(), request.getTimeSlotId(),
                        request.getRoomId(), request.getAcademicSession())) {
            throw new RuntimeException(
                    "Room conflict: " + room.getRoomNumber() +
                            " is already booked on " + request.getDayOfWeek() +
                            " at " + timeSlot.getLabel());
        }

        // 2. Faculty conflict — same faculty, same day, same time
        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingFacultyIdAndAcademicSession(
                        request.getDayOfWeek(), request.getTimeSlotId(),
                        offering.getFaculty().getId(), request.getAcademicSession())) {
            throw new RuntimeException(
                    "Faculty conflict: " + offering.getFaculty().getFullName() +
                            " already has a class on " + request.getDayOfWeek() +
                            " at " + timeSlot.getLabel());
        }

        // 3. Section conflict — same section, same day, same time
        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingSectionIdAndAcademicSession(
                        request.getDayOfWeek(), request.getTimeSlotId(),
                        offering.getSection().getId(), request.getAcademicSession())) {
            throw new RuntimeException(
                    "Section conflict: This section already has a class on " +
                            request.getDayOfWeek() + " at " + timeSlot.getLabel());
        }

        TimetableEntry entry = TimetableEntry.builder()
                .dayOfWeek(request.getDayOfWeek())
                .timeSlot(timeSlot)
                .room(room)
                .subjectOffering(offering)
                .academicSession(request.getAcademicSession())
                .build();

        return mapEntryToResponse(timetableEntryRepository.save(entry));
    }

    public List<TimetableEntryResponse> getTimetableBySection(
            Long sectionId, String academicSession) {
        return timetableEntryRepository
                .findBySubjectOfferingSectionIdAndAcademicSession(sectionId, academicSession)
                .stream()
                .map(this::mapEntryToResponse)
                .toList();
    }

    public List<TimetableEntryResponse> getTimetableByFaculty(
            Long facultyId, String academicSession) {
        return timetableEntryRepository
                .findBySubjectOfferingFacultyIdAndAcademicSession(facultyId, academicSession)
                .stream()
                .map(this::mapEntryToResponse)
                .toList();
    }

    @Transactional
    public void deleteEntry(Long id) {
        TimetableEntry entry = timetableEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Timetable entry not found with id: " + id));
        timetableEntryRepository.delete(entry);
    }

    // ─── Mappers ──────────────────────────────────────────────

    private RoomResponse mapRoomToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .capacity(room.getCapacity())
                .roomType(room.getRoomType())
                .active(room.isActive())
                .build();
    }

    private TimeSlotResponse mapSlotToResponse(TimeSlot slot) {
        return TimeSlotResponse.builder()
                .id(slot.getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .label(slot.getLabel())
                .build();
    }

    private TimetableEntryResponse mapEntryToResponse(TimetableEntry entry) {
        return TimetableEntryResponse.builder()
                .id(entry.getId())
                .dayOfWeek(entry.getDayOfWeek())
                .timeSlotId(entry.getTimeSlot().getId())
                .timeSlotLabel(entry.getTimeSlot().getLabel())
                .startTime(entry.getTimeSlot().getStartTime())
                .endTime(entry.getTimeSlot().getEndTime())
                .roomId(entry.getRoom().getId())
                .roomNumber(entry.getRoom().getRoomNumber())
                .roomType(entry.getRoom().getRoomType())
                .subjectOfferingId(entry.getSubjectOffering().getId())
                .subjectName(entry.getSubjectOffering().getSubject().getName())
                .subjectCode(entry.getSubjectOffering().getSubject().getCode())
                .facultyName(entry.getSubjectOffering().getFaculty().getFullName())
                .sectionName(entry.getSubjectOffering().getSection().getName())
                .academicSession(entry.getAcademicSession())
                .build();
    }
}