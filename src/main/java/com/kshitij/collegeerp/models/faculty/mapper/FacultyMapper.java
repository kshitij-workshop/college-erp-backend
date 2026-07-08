package com.kshitij.collegeerp.models.faculty.mapper;

import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultySummaryResponse;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import org.springframework.stereotype.Component;

@Component
public class FacultyMapper {

    public FacultyResponse toResponse(Faculty faculty) {

        if (faculty == null) {
            return null;
        }

        return FacultyResponse.builder()

                .id(faculty.getId())

                .userId(
                        faculty.getUser() != null
                                ? faculty.getUser().getId()
                                : null
                )

                // Identity

                .employeeCode(faculty.getEmployeeCode())

                // Personal Information

                .fullName(faculty.getFullName())
                .email(faculty.getEmail())
                .phone(faculty.getPhone())
                .gender(faculty.getGender())
                .dateOfBirth(faculty.getDateOfBirth())
                .bloodGroup(faculty.getBloodGroup())
                .address(faculty.getAddress())
                .photoUrl(faculty.getPhotoUrl())

                // Professional Information

                .designation(faculty.getDesignation())
                .qualification(faculty.getQualification())
                .specialization(faculty.getSpecialization())
                .experienceYears(faculty.getExperienceYears())
                .joiningDate(faculty.getJoiningDate())

                // Academic Information

                .departmentId(faculty.getDepartment().getId())
                .departmentName(faculty.getDepartment().getName())

                // Status

                .status(faculty.getStatus())

                .build();
    }

    public FacultySummaryResponse toSummaryResponse(Faculty faculty) {

        if (faculty == null) {
            return null;
        }

        return FacultySummaryResponse.builder()

                .id(faculty.getId())

                .employeeCode(faculty.getEmployeeCode())

                .fullName(faculty.getFullName())
                .email(faculty.getEmail())
                .phone(faculty.getPhone())

                .designation(faculty.getDesignation())

                .joiningDate(faculty.getJoiningDate())

                .departmentId(faculty.getDepartment().getId())
                .departmentName(faculty.getDepartment().getName())

                .status(faculty.getStatus())

                .build();
    }

}