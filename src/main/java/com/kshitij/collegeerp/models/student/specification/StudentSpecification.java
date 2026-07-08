package com.kshitij.collegeerp.models.student.specification;

import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.entity.StudentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StudentSpecification {

    public static Specification<Student> search(
            String keyword,
            Long departmentId,
            Long programId,
            Long batchId,
            Long semesterId,
            Long sectionId,
            StudentStatus status
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ==========================
            // Keyword Search
            // ==========================

            if (keyword != null && !keyword.isBlank()) {

                String search = "%" + keyword.toLowerCase() + "%";

                predicates.add(

                        cb.or(

                                cb.like(
                                        cb.lower(root.get("fullName")),
                                        search
                                ),

                                cb.like(
                                        cb.lower(root.get("email")),
                                        search
                                ),

                                cb.like(
                                        cb.lower(root.get("enrollmentNumber")),
                                        search
                                )

                        )

                );

            }

            // ==========================
            // Department
            // ==========================

            if (departmentId != null) {

                predicates.add(
                        cb.equal(
                                root.get("department").get("id"),
                                departmentId
                        )
                );

            }

            // ==========================
            // Program
            // ==========================

            if (programId != null) {

                predicates.add(
                        cb.equal(
                                root.get("program").get("id"),
                                programId
                        )
                );

            }

            // ==========================
            // Batch
            // ==========================

            if (batchId != null) {

                predicates.add(
                        cb.equal(
                                root.get("batch").get("id"),
                                batchId
                        )
                );

            }

            // ==========================
            // Semester
            // ==========================

            if (semesterId != null) {

                predicates.add(
                        cb.equal(
                                root.get("semester").get("id"),
                                semesterId
                        )
                );

            }

            // ==========================
            // Section
            // ==========================

            if (sectionId != null) {

                predicates.add(
                        cb.equal(
                                root.get("section").get("id"),
                                sectionId
                        )
                );

            }

            // ==========================
            // Status
            // ==========================

            if (status != null) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                status
                        )
                );

            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }

}