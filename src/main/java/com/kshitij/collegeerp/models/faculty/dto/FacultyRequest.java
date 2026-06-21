// FacultyRequest.java
package com.kshitij.collegeerp.models.faculty.dto;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class FacultyRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message = "Designation is required")
    private Designation designation;

    private String qualification;

    private String specialization;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private LocalDate joiningDate;
}