package com.kshitij.collegeerp.models.notice.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.notice.dto.NoticeRequest;
import com.kshitij.collegeerp.models.notice.dto.NoticeResponse;
import com.kshitij.collegeerp.models.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<NoticeResponse>> create(
            @Valid @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Notice created successfully",
                        noticeService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Notices fetched successfully",
                        noticeService.getAll()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getForStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Notices fetched successfully",
                        noticeService.getMyNoticesAsStudent(studentId)));
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getForFaculty(
            @PathVariable Long facultyId) {
        return ResponseEntity.ok(
                ApiResponse.success("Notices fetched successfully",
                        noticeService.getMyNoticesAsFaculty(facultyId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Notice fetched successfully",
                        noticeService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<NoticeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Notice updated successfully",
                        noticeService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        noticeService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Notice deactivated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Notice deleted successfully", null));
    }
}