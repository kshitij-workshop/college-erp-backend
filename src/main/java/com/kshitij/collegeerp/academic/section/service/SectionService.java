package com.kshitij.collegeerp.academic.section.service;

import com.kshitij.collegeerp.academic.batch.entity.Batch;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.section.dto.SectionRequest;
import com.kshitij.collegeerp.academic.section.dto.SectionResponse;
import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.academic.section.repository.SectionRepository;
import com.kshitij.collegeerp.academic.semester.dto.SemesterRequest;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import com.kshitij.collegeerp.academic.semester.repository.SemesterRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SemesterRepository semesterRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public SectionResponse create(SectionRequest request) {

        if (sectionRepository.existsByNameAndSemesterId(
                request.getName(),
                request.getSemesterId())) {

            throw new IllegalArgumentException(
                    "Section " + request.getName()
                            + " already exists for this semester."
            );
        }

        Semester semester = getSemester(request.getSemesterId());

        Section section = Section.builder()
                .name(request.getName())
                .maxStrength(request.getMaxStrength())
                .semester(semester)
                .active(true)
                .build();

        Section saved = sectionRepository.save(section);

        return mapToResponse(saved);
    }

    public List<SectionResponse> getAll() {
        return sectionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SectionResponse> getBySemester(Long semesterId) {
        return sectionRepository.findBySemesterId(semesterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SectionResponse getById(Long id) {
        Section section = findSectionById(id);
        return mapToResponse(section);
    }

    @Transactional
    public SectionResponse update(
            Long id,
            SectionRequest request
    ) {

        Section section = findSectionById(id);

        if ((!section.getName().equalsIgnoreCase(request.getName())
                || !section.getSemester().getId().equals(request.getSemesterId()))
                && sectionRepository.existsByNameAndSemesterId(
                request.getName(),
                request.getSemesterId())) {

            throw new IllegalArgumentException(
                    "Section " + request.getName()
                            + " already exists for this semester."
            );
        }

        Semester semester = getSemester(request.getSemesterId());

        section.setName(request.getName());
        section.setMaxStrength(request.getMaxStrength());
        section.setSemester(semester);

        Section updated = sectionRepository.save(section);

        return mapToResponse(updated);
    }

    @Transactional
    public void deactivate(Long id) {
        Section section = findSectionById(id);
        section.setActive(false);
        sectionRepository.save(section);
    }

    @Transactional
    public void delete(Long id) {
        Section section = findSectionById(id);
        sectionRepository.delete(section);
    }

    private Section findSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + id));
    }

    private SectionResponse mapToResponse(Section section) {

        Semester semester = section.getSemester();

        Batch batch = semester.getBatch();

        Program program = batch.getProgram();

        Department department = program.getDepartment();

        return SectionResponse.builder()

                .id(section.getId())

                .name(section.getName())

                .maxStrength(section.getMaxStrength())

                .departmentId(department.getId())
                .departmentName(department.getName())

                .programId(program.getId())
                .programName(program.getName())

                .batchId(batch.getId())
                .batchName(batch.getName())

                .semesterId(semester.getId())
                .semesterNumber(semester.getSemesterNumber())

                .active(section.isActive())

                .build();
    }

    private Semester getSemester(Long semesterId) {

        return semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with id " + semesterId
                ));

    }
}
