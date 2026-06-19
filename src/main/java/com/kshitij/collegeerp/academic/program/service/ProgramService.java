package com.kshitij.collegeerp.academic.program.service;


import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.academic.program.dto.ProgramRequest;
import com.kshitij.collegeerp.academic.program.dto.ProgramResponse;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;

    public ProgramResponse create(ProgramRequest request) {
        if(programRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Program code already exists " + request.getCode());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id " + request.getDepartmentId()
                ));

        Program program = Program.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .durationYear(request.getDurationYear())
                .totalSemesters(request.getTotalSemesters())
                .department(department)
                .active(true)
                .build();

        Program saved = programRepository.save(program);

        return mapToResponse(saved);
    }

    public List<ProgramResponse> getAll() {
        return programRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProgramResponse> getByDepartment(Long departmentId) {
        return programRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProgramResponse getById(Long id) {
        Program program = findProgramById(id);
        return mapToResponse(program);
    }


    @Transactional
    public ProgramResponse update(Long id, ProgramRequest request) {
        Program program = findProgramById(id);
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id " + request.getDepartmentId()
                ));

        program.setName(request.getName());
        program.setDepartment(department);
        program.setDurationYear(request.getDurationYear());
        program.setTotalSemesters(request.getTotalSemesters());
        program.setCode(request.getCode().toUpperCase());

        Program updated = programRepository.save(program);

        return mapToResponse(updated);
    }

    @Transactional
    public void deactivate(Long id) {
        Program program = findProgramById(id);
        program.setActive(false);
        programRepository.save(program);
    }

    @Transactional
    public void delete(Long id) {
        Program program = findProgramById(id);
        programRepository.delete(program);
    }


    private Program findProgramById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Program not found with id " + id
                ));
    }


    private ProgramResponse mapToResponse(Program program) {
        return ProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .code(program.getCode())
                .durationYear(program.getDurationYear())
                .totalSemester(program.getTotalSemesters())
                .departmentId(program.getDepartment().getId())
                .departmentName(program.getDepartment().getName())
                .active(program.isActive())
                .build();
    }

}
