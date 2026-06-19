package com.kshitij.collegeerp.academic.section.controller;

import com.kshitij.collegeerp.academic.section.dto.SectionRequest;
import com.kshitij.collegeerp.academic.section.dto.SectionResponse;
import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.academic.section.service.SectionService;
import com.kshitij.collegeerp.academic.semester.service.SemesterService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> create(
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Section created successfully",
                        sectionService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Sections fetched successfully",
                        sectionService.getAll())
        );
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(
                ApiResponse.success("Sections fetched successfully",
                        sectionService.getBySemester(semesterId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SectionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Section fetched successfuly",
                        sectionService.getById(id))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        sectionService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Seciton deactivated successfully", null)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Section deleted successfully", null)
        );
    }
}
