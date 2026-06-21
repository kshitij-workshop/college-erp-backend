package com.kshitij.collegeerp.models.student.dto;

import com.kshitij.collegeerp.models.student.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter

public class StudentRequest {
    @Setter
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
        private String phone;

        private Gender gender;

        private LocalDate dateOfBirth;

        private String bloodGroup;

        private String address;

        private String guardianName;
        private String guardianPhone;
        private String guardianRelation;

        @NotNull(message = "Department ID is required")
        private Long departmentId;

        @NotNull(message = "Program ID is required")
        private Long programId;

        @NotNull(message = "Batch ID is required")
        private Long batchId;

        @NotNull(message = "Semester ID is required")
        private Long semesterId;

        @NotNull(message = "Section ID is required")
        private Long sectionId;

        private LocalDate admissionDate;
}
