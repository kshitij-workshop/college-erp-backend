package com.kshitij.collegeerp.models.student.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.student.dto.StudentRequest;
import com.kshitij.collegeerp.models.student.dto.StudentResponse;
import com.kshitij.collegeerp.models.student.entity.StudentStatus;
import com.kshitij.collegeerp.models.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> create(
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Student created successfully",
                        studentService.create(request)));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getAllStudents(

            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(required = false) Long departmentId,

            @RequestParam(required = false) Long programId,

            @RequestParam(required = false) Long batchId,

            @RequestParam(required = false) Long semesterId,

            @RequestParam(required = false) Long sectionId,

            @RequestParam(required = false) StudentStatus status,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Students fetched successfully",
                        studentService.getAllStudents(
                                pageable,
                                keyword,
                                departmentId,
                                programId,
                                batchId,
                                semesterId,
                                sectionId,
                                status
                        )
                )
        );
    }



    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getBySection(
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(
                ApiResponse.success("Students fetched successfully",
                        studentService.getBySection(sectionId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Student fetched successfully",
                        studentService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Student updated successfully",
                        studentService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam StudentStatus status) {
        studentService.updateStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Student status updated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Student deleted successfully", null));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponse>> me(Authentication authentication) {
        return ResponseEntity.ok(
                ApiResponse.success("Student fetched successfully", studentService.getCurrentStudent(authentication))
        );
    }

}
