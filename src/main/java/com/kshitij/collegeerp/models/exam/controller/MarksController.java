package com.kshitij.collegeerp.models.exam.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.exam.dto.MarksEntryRequest;
import com.kshitij.collegeerp.models.exam.dto.ResultResponse;
import com.kshitij.collegeerp.models.exam.service.MarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarksController {

    private final MarksService marksService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> enterMarks(
            @Valid @RequestBody MarksEntryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Marks entered successfully", marksService.enterMarks(request))
        );
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(
                ApiResponse.success("Results fetched successfully", marksService.getResultsByExam(examId))
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "true") boolean onlyPublished) {
        return ResponseEntity.ok(
                ApiResponse.success("Results fetched successfully", marksService.getResultsByStudent(studentId, onlyPublished))
        );
    }
}
