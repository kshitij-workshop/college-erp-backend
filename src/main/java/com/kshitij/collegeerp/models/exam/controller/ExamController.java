package com.kshitij.collegeerp.models.exam.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.exam.dto.ExamRequest;
import com.kshitij.collegeerp.models.exam.dto.ExamResponse;
import com.kshitij.collegeerp.models.exam.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<ExamResponse>> create(
            @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Exam created successfully", examService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Exams fetched successfully", examService.getAll())
        );
    }

    @GetMapping("/offering/{subjectOfferingId}")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getBySubjectOffering(
            @PathVariable Long subjectOfferingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Exams fetched successfully",
                        examService.getBySubjectOffering(subjectOfferingId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Exam fetched successfully",
                        examService.getById(id))
        );
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> lock(@PathVariable Long id) {
        examService.lockExam(id);
        return ResponseEntity.ok(
                ApiResponse.success("Exam locked successfylly", null)
        );
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> publish(@PathVariable Long id) {
        examService.publishResult(id);
        return ResponseEntity.ok(
                ApiResponse.success("Result published successfully", null)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Exam deleted successfully", null)
        );
    }

}
