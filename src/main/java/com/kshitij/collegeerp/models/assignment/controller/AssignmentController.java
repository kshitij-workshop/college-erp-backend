package com.kshitij.collegeerp.models.assignment.controller;

import com.kshitij.collegeerp.models.assignment.dto.*;
import com.kshitij.collegeerp.models.assignment.service.AssignmentService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> create(
            @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignment created successfully",
                        assignmentService.create(request)));
    }

    @GetMapping("/offering/{subjectOfferingId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getByOffering(
            @PathVariable Long subjectOfferingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignments fetched successfully",
                        assignmentService.getBySubjectOffering(subjectOfferingId)));
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getBySection(
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignments fetched successfully",
                        assignmentService.getBySection(sectionId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignment fetched successfully",
                        assignmentService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignment updated successfully",
                        assignmentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Assignment deleted successfully", null));
    }

    // ─── Submission endpoints ──────────────────────────────

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submit(
            @Valid @RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Assignment submitted successfully",
                        assignmentService.submit(request)));
    }

    @PatchMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> grade(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Submission graded successfully",
                        assignmentService.grade(submissionId, request)));
    }

    @GetMapping("/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Submissions fetched successfully",
                        assignmentService.getSubmissionsByAssignment(assignmentId)));
    }

    @GetMapping("/submissions/student/{studentId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Submissions fetched successfully",
                        assignmentService.getSubmissionsByStudent(studentId)));
    }
}