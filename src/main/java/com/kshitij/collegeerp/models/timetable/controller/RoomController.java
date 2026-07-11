package com.kshitij.collegeerp.models.timetable.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.timetable.dto.RoomRequest;
import com.kshitij.collegeerp.models.timetable.dto.RoomResponse;
import com.kshitij.collegeerp.models.timetable.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Room added successfully",
                        roomService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        return ResponseEntity.ok(
                ApiResponse.success("Rooms fetched successfully",
                        roomService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomsById(@PathVariable Long id){
        return ResponseEntity.ok(
                ApiResponse.success("Room fetched successfully",
                        roomService.getById(id))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable Long id,
                                                                @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Room updated successfully",
                        roomService.update(id, request))
        );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id){
        roomService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room deleted successfully", null)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> activateRoom(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Room activated successfully", roomService.activate(id))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> deactivateRoom(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Room deactivated successfully", roomService.deactivate(id)));
    }
}
