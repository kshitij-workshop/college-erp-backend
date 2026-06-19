package com.kshitij.collegeerp.academic.department.service;

import com.kshitij.collegeerp.academic.department.dto.DepartmentRequest;
import com.kshitij.collegeerp.academic.department.dto.DepartmentResponse;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentResponse create(DepartmentRequest request) {
        if(departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }
        if(departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .active(true)
                .build();

        Department saved = departmentRepository.save(department);

        return mapToResponse(saved);
    }

    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DepartmentResponse getById(Long id) {
        Department department = findDepartmentById(id);
        return mapToResponse(department);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartmentById(id);

        department.setName(request.getName());
        department.setCode(request.getCode().toUpperCase());
        department.setDescription(request.getDescription());

        Department updated = departmentRepository.save(department);

        return mapToResponse(updated);
    }


    @Transactional
    public void deactivate(Long id) {
        Department department = findDepartmentById(id);
        department.setActive(false);
        departmentRepository.save(department);
    }

    @Transactional
    public void delete(Long id) {
        Department department = findDepartmentById(id);
        departmentRepository.delete(department);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .active(department.isActive())
                .build();
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));
    }

}
