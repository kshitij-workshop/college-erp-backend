package com.kshitij.collegeerp.models.subject.controller;

import com.kshitij.collegeerp.academic.program.dto.ProgramResponse;
import com.kshitij.collegeerp.academic.program.service.ProgramService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.subject.dto.SubjectRequest;
import com.kshitij.collegeerp.models.subject.dto.SubjectResponse;
import com.kshitij.collegeerp.models.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> create(
            @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject created successfully",
                        subjectService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Subjects fetched successfully",
                        subjectService.getAll()));
    }

    @GetMapping("/program/{programId}/semester/{semesterNumber}")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getByProgramAndSemester(
            @PathVariable Long programId,
            @PathVariable Integer semesterNumber) {
        return ResponseEntity.ok(
                ApiResponse.success("Subjects fetched successfully",
                        subjectService.getByProgramAndSemester(programId, semesterNumber)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject fetched successfully",
                        subjectService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject updated successfully",
                        subjectService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        subjectService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Subject deactivated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Subject deleted successfully", null));
    }
}