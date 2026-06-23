package com.kshitij.collegeerp.models.timetable.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.timetable.dto.*;
import com.kshitij.collegeerp.models.timetable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    // ─── Room ──────────────────────────────────────────────

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Room added successfully",
                        timetableService.addRoom(request)));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        return ResponseEntity.ok(
                ApiResponse.success("Rooms fetched successfully",
                        timetableService.getAllRooms()));
    }

    // ─── Time Slot ─────────────────────────────────────────

    @PostMapping("/time-slots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> addTimeSlot(
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot added successfully",
                        timetableService.addTimeSlot(request)));
    }

    @GetMapping("/time-slots")
    public ResponseEntity<ApiResponse<List<TimeSlotResponse>>> getAllTimeSlots() {
        return ResponseEntity.ok(
                ApiResponse.success("Time slots fetched successfully",
                        timetableService.getAllTimeSlots()));
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