package com.kshitij.collegeerp.models.faculty.service;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.common.response.PageResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultyRequest;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultySummaryResponse;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface FacultyService {

    FacultyResponse createFaculty(FacultyRequest request);

    FacultyResponse updateFaculty(
            Long facultyId,
            FacultyRequest request
    );

    FacultyResponse getFacultyById(Long facultyId);

    PageResponse<FacultySummaryResponse> getAllFaculty(
            String search,
            Long departmentId,
            Designation designation,
            FacultyStatus status,
            Pageable pageable
    );

    void deleteFaculty(Long facultyId);

    FacultyResponse getCurrentFaculty(Authentication authentication);
}