package com.kshitij.collegeerp.models.timetable.specification;

import com.kshitij.collegeerp.models.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.domain.Specification;

public class TimetableSpecification {

    public static Specification<TimetableEntry> hasProgram(Long programId) {

        return (root, query, cb) ->

                programId == null

                        ? cb.conjunction()

                        : cb.equal(
                        root.get("subjectOffering")
                                .get("section")
                                .get("semester")
                                .get("batch")
                                .get("program")
                                .get("id"),
                        programId
                );
    }

    public static Specification<TimetableEntry> hasSemester(Long semesterId) {

        return (root, query, cb) ->

                semesterId == null

                        ? cb.conjunction()

                        : cb.equal(
                        root.get("subjectOffering")
                                .get("section")
                                .get("semester")
                                .get("id"),
                        semesterId
                );
    }

    public static Specification<TimetableEntry> hasSection(Long sectionId) {

        return (root, query, cb) ->

                sectionId == null

                        ? cb.conjunction()

                        : cb.equal(
                        root.get("subjectOffering")
                                .get("section")
                                .get("id"),
                        sectionId
                );
    }

    public static Specification<TimetableEntry> hasFaculty(Long facultyId) {

        return (root, query, cb) ->

                facultyId == null

                        ? cb.conjunction()

                        : cb.equal(
                        root.get("subjectOffering")
                                .get("faculty")
                                .get("id"),
                        facultyId
                );
    }

    public static Specification<TimetableEntry> hasSession(
            String academicSession
    ) {

        return (root, query, cb) ->

                academicSession == null || academicSession.isBlank()

                        ? cb.conjunction()

                        : cb.equal(
                        root.get("academicSession"),
                        academicSession
                );
    }

}