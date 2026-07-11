package com.kshitij.collegeerp.models.timetable.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import com.kshitij.collegeerp.models.timetable.dto.TimetableEntryRequest;
import com.kshitij.collegeerp.models.timetable.dto.TimetableEntryResponse;
import com.kshitij.collegeerp.models.timetable.entity.Room;
import com.kshitij.collegeerp.models.timetable.entity.TimeSlot;
import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import com.kshitij.collegeerp.models.timetable.repository.RoomRepository;
import com.kshitij.collegeerp.models.timetable.repository.TimeSlotRepository;
import com.kshitij.collegeerp.models.timetable.repository.TimetableEntryRepository;
import com.kshitij.collegeerp.models.timetable.specification.TimetableSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    // =========================================================
    // Create Timetable Entry
    // =========================================================

    @Transactional
    public TimetableEntryResponse createEntry(
            TimetableEntryRequest request
    ) {

        TimeSlot timeSlot = getTimeSlot(request.getTimeSlotId());

        Room room = getRoom(request.getRoomId());

        SubjectOffering offering =
                getSubjectOffering(request.getSubjectOfferingId());

        validateCreateConflict(
                request,
                offering
        );

        TimetableEntry entry = TimetableEntry.builder()
                .dayOfWeek(request.getDayOfWeek())
                .timeSlot(timeSlot)
                .room(room)
                .subjectOffering(offering)
                .academicSession(request.getAcademicSession())
                .build();

        return mapToResponse(
                timetableEntryRepository.save(entry)
        );
    }

    // =========================================================
    // Update Timetable Entry
    // =========================================================

    @Transactional
    public TimetableEntryResponse updateEntry(
            Long id,
            TimetableEntryRequest request
    ) {

        TimetableEntry entry =
                timetableEntryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Timetable entry not found with id: "
                                                + id));

        TimeSlot timeSlot = getTimeSlot(request.getTimeSlotId());

        Room room = getRoom(request.getRoomId());

        SubjectOffering offering =
                getSubjectOffering(request.getSubjectOfferingId());

        validateUpdateConflict(
                id,
                request,
                offering
        );

        entry.setDayOfWeek(request.getDayOfWeek());
        entry.setTimeSlot(timeSlot);
        entry.setRoom(room);
        entry.setSubjectOffering(offering);
        entry.setAcademicSession(request.getAcademicSession());

        return mapToResponse(
                timetableEntryRepository.save(entry)
        );
    }

    // =========================================================
    // Get Timetable Entry By Id
    // =========================================================

    public TimetableEntryResponse getEntryById(Long id) {

        TimetableEntry entry = timetableEntryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Timetable entry not found with id: " + id));

        return mapToResponse(entry);
    }

    // =========================================================
    // Get All Timetable Entries
    // =========================================================
    public List<TimetableEntryResponse> getAllEntries(

            Long programId,

            Long semesterId,

            Long sectionId,

            Long facultyId,

            String academicSession
    ) {

        Specification<TimetableEntry> specification =
                Specification
                        .where(TimetableSpecification.hasProgram(programId))
                        .and(TimetableSpecification.hasSemester(semesterId))
                        .and(TimetableSpecification.hasSection(sectionId))
                        .and(TimetableSpecification.hasFaculty(facultyId))
                        .and(TimetableSpecification.hasSession(academicSession));

        return timetableEntryRepository.findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("dayOfWeek"),
                                Sort.Order.asc("timeSlot.startTime")
                        )
                ).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // Get Timetable By Section
    // =========================================================

    public List<TimetableEntryResponse> getTimetableBySection(
            Long sectionId,
            String academicSession
    ) {

        return timetableEntryRepository
                .findBySubjectOfferingSectionIdAndAcademicSessionOrderByDayOfWeekAscTimeSlotStartTimeAsc(
                        sectionId,
                        academicSession
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // Get Timetable By Faculty
    // =========================================================

    public List<TimetableEntryResponse> getTimetableByFaculty(
            Long facultyId,
            String academicSession
    ) {

        return timetableEntryRepository
                .findBySubjectOfferingFacultyIdAndAcademicSessionOrderByDayOfWeekAscTimeSlotStartTimeAsc(
                        facultyId,
                        academicSession
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // Delete Timetable Entry
    // =========================================================

    @Transactional
    public void deleteEntry(Long id) {

        TimetableEntry entry = timetableEntryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Timetable entry not found with id: " + id));

        timetableEntryRepository.delete(entry);
    }

    // =========================================================
    // Conflict Validation - Create
    // =========================================================

    private void validateCreateConflict(
            TimetableEntryRequest request,
            SubjectOffering offering
    ) {

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndRoomIdAndAcademicSession(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        request.getRoomId(),
                        request.getAcademicSession())) {

            throw new RuntimeException(
                    "Room is already occupied during this time slot."
            );
        }

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingFacultyIdAndAcademicSession(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        offering.getFaculty().getId(),
                        request.getAcademicSession())) {

            throw new RuntimeException(
                    "Faculty already has another class during this time slot."
            );
        }

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingSectionIdAndAcademicSession(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        offering.getSection().getId(),
                        request.getAcademicSession())) {

            throw new RuntimeException(
                    "Section already has another class during this time slot."
            );
        }
    }

    // =========================================================
    // Conflict Validation - Update
    // =========================================================

    private void validateUpdateConflict(
            Long entryId,
            TimetableEntryRequest request,
            SubjectOffering offering
    ) {

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndRoomIdAndAcademicSessionAndIdNot(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        request.getRoomId(),
                        request.getAcademicSession(),
                        entryId)) {

            throw new RuntimeException(
                    "Room is already occupied during this time slot."
            );
        }

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingFacultyIdAndAcademicSessionAndIdNot(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        offering.getFaculty().getId(),
                        request.getAcademicSession(),
                        entryId)) {

            throw new RuntimeException(
                    "Faculty already has another class during this time slot."
            );
        }

        if (timetableEntryRepository
                .existsByDayOfWeekAndTimeSlotIdAndSubjectOfferingSectionIdAndAcademicSessionAndIdNot(
                        request.getDayOfWeek(),
                        request.getTimeSlotId(),
                        offering.getSection().getId(),
                        request.getAcademicSession(),
                        entryId)) {

            throw new RuntimeException(
                    "Section already has another class during this time slot."
            );
        }
    }

    // =========================================================
    // Helper Methods
    // =========================================================

    private Room getRoom(Long id) {

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));
    }

    private TimeSlot getTimeSlot(Long id) {

        return timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Time slot not found with id: " + id));
    }

    private SubjectOffering getSubjectOffering(Long id) {

        return subjectOfferingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject offering not found with id: " + id));
    }

    // =========================================================
    // Mapper
    // =========================================================

    private TimetableEntryResponse mapToResponse(
            TimetableEntry entry
    ) {

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

                .facultyId(entry.getSubjectOffering().getFaculty().getId())
                .facultyName(entry.getSubjectOffering().getFaculty().getFullName())

                .sectionId(entry.getSubjectOffering().getSection().getId())
                .sectionName(entry.getSubjectOffering().getSection().getName())

                .programId(
                        entry.getSubjectOffering()
                                .getSection()
                                .getSemester()
                                .getBatch()
                                .getProgram()
                                .getId()
                )

                .programName(
                        entry.getSubjectOffering()
                                .getSection()
                                .getSemester()
                                .getBatch()
                                .getProgram()
                                .getName()
                )

                .semesterId(
                        entry.getSubjectOffering()
                                .getSection()
                                .getSemester()
                                .getId()
                )

                .semesterNumber(
                        entry.getSubjectOffering()
                                .getSection()
                                .getSemester()
                                .getSemesterNumber()
                )

                .academicSession(entry.getAcademicSession())

                .build();
    }

}