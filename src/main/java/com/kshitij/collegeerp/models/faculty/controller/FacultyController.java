package com.kshitij.collegeerp.models.faculty.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultyRequest;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import com.kshitij.collegeerp.models.faculty.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FacultyResponse>> create(
            @Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty created successfully",
                        facultyService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOD')")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty list fetched successfully",
                        facultyService.getAll()));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOD')")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getByDepartment(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty fetched successfully",
                        facultyService.getByDepartment(departmentId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty fetched successfully",
                        facultyService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FacultyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty updated successfully",
                        facultyService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam FacultyStatus status) {
        facultyService.updateStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Faculty status updated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Faculty deleted successfully", null));
    }
}