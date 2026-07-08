package com.kshitij.collegeerp.models.faculty.dto;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRequest {

    // Personal Information

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be 10 digits"
    )
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private LocalDate dateOfBirth;

    private String bloodGroup;

    @Size(max = 500)
    private String address;

    // Professional Information

    @NotNull(message = "Designation is required")
    private Designation designation;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    private String specialization;

    @PositiveOrZero(message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private String photoUrl;

    // Academic Information

    @NotNull(message = "Department is required")
    private Long departmentId;

}