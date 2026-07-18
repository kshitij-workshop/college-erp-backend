package com.kshitij.collegeerp.models.subject.controller;

import com.kshitij.collegeerp.academic.section.dto.SectionResponse;
import com.kshitij.collegeerp.academic.section.service.SectionService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.subject.dto.SubjectOfferingRequest;
import com.kshitij.collegeerp.models.subject.dto.SubjectOfferingResponse;
import com.kshitij.collegeerp.models.subject.service.SubjectOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject-offerings")
@RequiredArgsConstructor
public class SubjectOfferingController {

    private final SubjectOfferingService subjectOfferingService;
    private final SectionService sectionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> create(
            @Valid @RequestBody SubjectOfferingRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offering created successfully",
                        subjectOfferingService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offerings fetched successfully",
                        subjectOfferingService.getAll()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>> getMyOfferings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Subject offerings fetched successfully",
                            subjectOfferingService.getAll()
                    )
            );
        }
        return ResponseEntity.ok(
                ApiResponse.success("Assigned subject offerings fetched successfully",
                        subjectOfferingService.getMyOfferings()));
    }

    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>> getByFaculty(
            @PathVariable Long facultyId) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offerings fetched successfully",
                        subjectOfferingService.getByFaculty(facultyId)));
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>> getBySection(
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offerings fetched successfully",
                        subjectOfferingService.getBySection(sectionId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offering fetched successfully",
                        subjectOfferingService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectOfferingRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Subject offering updated successfully",
                        subjectOfferingService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        subjectOfferingService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Subject offering deactivated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subjectOfferingService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Subject offering deleted successfully", null));
    }
}
