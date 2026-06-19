package com.kshitij.collegeerp.academic.semester.controller;

import com.kshitij.collegeerp.academic.semester.dto.SemesterRequest;
import com.kshitij.collegeerp.academic.semester.dto.SemesterResponse;
import com.kshitij.collegeerp.academic.semester.service.SemesterService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SemesterResponse>> create(
            @Valid @RequestBody SemesterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Semester created successfully",
                        semesterService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Semesters fetched successfully",
                        semesterService.getAll())
        );
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(
                ApiResponse.success("Semesters fetched successfully",
                        semesterService.getByBatch(batchId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Semester fetched successfully",
                        semesterService.getById(id))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SemesterResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SemesterRequest request){
        return ResponseEntity.ok(
                ApiResponse.success("Semester updated successfully",
                        semesterService.update(id, request))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        semesterService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Semester deactivated successfully",null)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        semesterService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Semester deleted successfully", null)
        );
    }

}
