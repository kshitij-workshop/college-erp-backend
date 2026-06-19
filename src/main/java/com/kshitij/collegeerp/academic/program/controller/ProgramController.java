package com.kshitij.collegeerp.academic.program.controller;


import com.kshitij.collegeerp.academic.program.dto.ProgramRequest;
import com.kshitij.collegeerp.academic.program.dto.ProgramResponse;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.service.ProgramService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> create(
            @Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Program Created Successfully", programService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Programs fetched successfully", programService.getAll())
        );
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<ProgramResponse>>> getByDepartment(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Programs fetched successfully", programService.getByDepartment(departmentId)));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgramResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Program fetched successfully", programService.getById(id))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Program updated successfully",
                        programService.update(id, request)
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        programService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Program deactivated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        programService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Program deleted successfully", null)
        );
    }



}
