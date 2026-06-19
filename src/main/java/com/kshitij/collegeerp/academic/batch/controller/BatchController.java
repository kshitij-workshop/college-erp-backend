package com.kshitij.collegeerp.academic.batch.controller;

import com.kshitij.collegeerp.academic.batch.dto.BatchRequest;
import com.kshitij.collegeerp.academic.batch.dto.BatchResponse;
import com.kshitij.collegeerp.academic.batch.service.BatchService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> create(
            @Valid @RequestBody BatchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Batch created successfully",
                        batchService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Batches fetched successfully", batchService.getAll())
        );
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(
                ApiResponse.success("Batches fetched successfully",
                        batchService.getByProgram(programId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BatchResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Batch fetched successfully", batchService.getById(id))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BatchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Batch updated successfully",
                        batchService.update(id, request))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        batchService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Batch deactivated successfully",null)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        batchService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Batch deleted successfully", null)
        );
    }



}
