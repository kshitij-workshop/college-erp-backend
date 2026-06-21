package com.kshitij.collegeerp.models.faculty.service;

import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.faculty.dto.FacultyRequest;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public FacultyResponse create(FacultyRequest request) {
        if (facultyRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (!department.isActive()) {
            throw new RuntimeException("Cannot assign faculty to an inactive department");
        }

        if (request.getJoiningDate() != null && request.getJoiningDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Joining date cannot be in the future");
        }

        if (request.getDesignation() == Designation.HOD) {
            boolean hodExists = facultyRepository.existsByDepartmentIdAndDesignationAndStatus(
                    request.getDepartmentId(), Designation.HOD, FacultyStatus.ACTIVE);
            if (hodExists) {
                throw new RuntimeException("This department already has an active HOD");
            }
        }


        String employeeCode = generateEmployeeCode(department.getCode());

        Faculty faculty = Faculty.builder()
                .employeeCode(employeeCode)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .qualification(request.getQualification())
                .specialization(request.getSpecialization())
                .department(department)
                .joiningDate(request.getJoiningDate() != null
                        ? request.getJoiningDate() : LocalDate.now())
                .status(FacultyStatus.ACTIVE)
                .build();

        Faculty saved = facultyRepository.save(faculty);
        return mapToResponse(saved);
    }

    public List<FacultyResponse> getAll() {
        return facultyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FacultyResponse> getByDepartment(Long departmentId) {
        return facultyRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FacultyResponse getById(Long id) {
        return mapToResponse(findFacultyById(id));
    }

    @Transactional
    public FacultyResponse update(Long id, FacultyRequest request) {
        Faculty faculty = findFacultyById(id);

        if (!faculty.getEmail().equals(request.getEmail())
                && facultyRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (!department.isActive()) {
            throw new RuntimeException("Cannot assign faculty to an inactive department");
        }

        if (request.getJoiningDate() != null && request.getJoiningDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Joining date cannot be in the future");
        }

        if (request.getDesignation() == Designation.HOD) {
            boolean hodExists = facultyRepository
                    .existsByDepartmentIdAndDesignationAndStatusAndIdNot(
                            request.getDepartmentId(), Designation.HOD,
                            FacultyStatus.ACTIVE, id);
            if (hodExists) {
                throw new RuntimeException("This department already has an active HOD");
            }
        }

        faculty.setFullName(request.getFullName());
        faculty.setEmail(request.getEmail());
        faculty.setPhone(request.getPhone());
        faculty.setDesignation(request.getDesignation());
        faculty.setQualification(request.getQualification());
        faculty.setSpecialization(request.getSpecialization());
        faculty.setDepartment(department);

        return mapToResponse(facultyRepository.save(faculty));
    }

    @Transactional
    public void updateStatus(Long id, FacultyStatus status) {
        Faculty faculty = findFacultyById(id);
        faculty.setStatus(status);
        facultyRepository.save(faculty);
    }

    @Transactional
    public void delete(Long id) {
        facultyRepository.delete(findFacultyById(id));
    }

    private Faculty findFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Faculty not found with id: " + id));
    }

    private String generateEmployeeCode(String departmentCode) {
        int year = Year.now().getValue();
        long count = facultyRepository.count() + 1;
        return String.format("EMP-%d%s%03d", year, departmentCode, count);
        // Example: EMP-2026CSE001
    }

    private FacultyResponse mapToResponse(Faculty faculty) {
        return FacultyResponse.builder()
                .id(faculty.getId())
                .employeeCode(faculty.getEmployeeCode())
                .fullName(faculty.getFullName())
                .email(faculty.getEmail())
                .phone(faculty.getPhone())
                .designation(faculty.getDesignation())
                .qualification(faculty.getQualification())
                .specialization(faculty.getSpecialization())
                .departmentId(faculty.getDepartment().getId())
                .departmentName(faculty.getDepartment().getName())
                .joiningDate(faculty.getJoiningDate())
                .status(faculty.getStatus())
                .build();
    }
}