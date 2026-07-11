package com.kshitij.collegeerp.models.timetable.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.timetable.dto.*;
import com.kshitij.collegeerp.models.timetable.service.RoomService;
import com.kshitij.collegeerp.models.timetable.service.TimeSlotService;
import com.kshitij.collegeerp.models.timetable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final TimeSlotService timeSlotService;

    // ─── Time Slot ─────────────────────────────────────────

    @PostMapping("/time-slots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> addTimeSlot(
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot added successfully",
                        timeSlotService.create(request)));
    }

    @PutMapping("/time-slots/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> updateTimeSlot(
            @PathVariable Long id,
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot updated successfully",
                        timeSlotService.update(id, request)));
    }

    @GetMapping("/time-slots")
    public ResponseEntity<ApiResponse<List<TimeSlotResponse>>> getAllTimeSlots() {
        return ResponseEntity.ok(
                ApiResponse.success("Time slots fetched successfully",
                        timeSlotService.getAll()));
    }

    @GetMapping("/time-slots/{id}")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> getTimeSlotById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot fetched successfully", timeSlotService.getById(id))
        );
    }

    @DeleteMapping("/time-slots/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Time Slot delted successfully", null)
        );
    }

    // ─── Timetable Entry ───────────────────────────────────

    @PostMapping("/entries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimetableEntryResponse>> createEntry(
            @Valid @RequestBody TimetableEntryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Timetable entry created successfully",
                        timetableService.createEntry(request)));
    }

    @GetMapping("entries")
    public ResponseEntity<ApiResponse<List<TimetableEntryResponse>>> getAllEntries(

            @RequestParam(required = false) Long programId,

            @RequestParam(required = false) Long semesterId,

            @RequestParam(required = false) Long sectionId,

            @RequestParam(required = false) Long facultyId,

            @RequestParam(required = false) String academicSession
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable fetched successfully",
                        timetableService.getAllEntries(
                                programId,
                                semesterId,
                                sectionId,
                                facultyId,
                                academicSession
                        )
                )
        );
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<TimetableEntryResponse>> getEntryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable entry fetched successfully",
                        timetableService.getEntryById(id)
                )
        );
    }

    @PutMapping("/entries/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimetableEntryResponse>> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody TimetableEntryRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable entry updated successfully",
                        timetableService.updateEntry(id, request)
                )
        );
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<TimetableEntryResponse>>> getBySection(
            @PathVariable Long sectionId,
            @RequestParam String academicSession) {
        return ResponseEntity.ok(
                ApiResponse.success("Timetable fetched successfully",
                        timetableService.getTimetableBySection(sectionId, academicSession)));
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<TimetableEntryResponse>>> getByFaculty(
            @PathVariable Long facultyId,
            @RequestParam String academicSession) {
        return ResponseEntity.ok(
                ApiResponse.success("Timetable fetched successfully",
                        timetableService.getTimetableByFaculty(facultyId, academicSession)));
    }

    @DeleteMapping("/entries/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(@PathVariable Long id) {
        timetableService.deleteEntry(id);
        return ResponseEntity.ok(
                ApiResponse.success("Timetable entry deleted successfully", null));
    }
}