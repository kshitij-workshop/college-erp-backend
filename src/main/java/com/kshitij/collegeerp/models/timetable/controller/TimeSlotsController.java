package com.kshitij.collegeerp.models.timetable.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.timetable.dto.TimeSlotRequest;
import com.kshitij.collegeerp.models.timetable.dto.TimeSlotResponse;
import com.kshitij.collegeerp.models.timetable.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotsController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> addTimeSlot(
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot added successfully",
                        timeSlotService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> updateTimeSlot(
            @PathVariable Long id,
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot updated successfully",
                        timeSlotService.update(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeSlotResponse>>> getAllTimeSlots() {
        return ResponseEntity.ok(
                ApiResponse.success("Time slots fetched successfully",
                        timeSlotService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> getTimeSlotById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Time slot fetched successfully", timeSlotService.getById(id))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Time Slot delted successfully", null)
        );
    }

}
