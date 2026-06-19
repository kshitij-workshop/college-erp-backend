package com.kshitij.collegeerp.academic.department.controller;

import com.kshitij.collegeerp.academic.department.dto.DepartmentRequest;
import com.kshitij.collegeerp.academic.department.dto.DepartmentResponse;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.service.DepartmentService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Department Created Successfully",
                        departmentService.create(request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Departments Fetched Successfully",
                        departmentService.getAll())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Department Fetched Successfully",
                        departmentService.getById(id))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Department Updated Successfully",
                        departmentService.update(id, request))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        departmentService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Department Deactivated Successfully", null)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Department Deleted Successfully", null)
        );
    }


}
