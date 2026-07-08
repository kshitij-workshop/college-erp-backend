package com.kshitij.collegeerp.models.faculty.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.common.response.PageResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultyRequest;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultySummaryResponse;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import com.kshitij.collegeerp.models.faculty.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasAnyRole('ADMIN')")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponse>> createFaculty(

            @Valid @RequestBody FacultyRequest request

    ) {

        FacultyResponse response = facultyService.createFaculty(request);

        return ResponseEntity.status(HttpStatus.CREATED)

                .body(

                        ApiResponse.success(

                                "Faculty created successfully.",

                                response

                        )

                );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyById(

            @PathVariable Long id

    ) {

        FacultyResponse response = facultyService.getFacultyById(id);

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Faculty fetched successfully.",

                        response

                )

        );

    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FacultySummaryResponse>>> getAllFaculty(

            @RequestParam(defaultValue = "") String search,

            @RequestParam(required = false) Long departmentId,

            @RequestParam(required = false) Designation designation,

            @RequestParam(required = false) FacultyStatus status,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        PageResponse<FacultySummaryResponse> response =

                facultyService.getAllFaculty(

                        search,

                        departmentId,

                        designation,

                        status,

                        pageable

                );

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Faculty fetched successfully.",

                        response

                )

        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> updateFaculty(

            @PathVariable Long id,

            @Valid @RequestBody FacultyRequest request

    ) {

        FacultyResponse response =

                facultyService.updateFaculty(id, request);

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Faculty updated successfully.",

                        response

                )

        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaculty(

            @PathVariable Long id

    ) {

        facultyService.deleteFaculty(id);

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Faculty deleted successfully.",

                        null

                )

        );

    }

}