package com.kshitij.collegeerp.academic.program.entity;

import com.kshitij.collegeerp.academic.department.entity.Department;
import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.Lazy;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Table(name = "programs")
@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // B.Tech - Computer Science and Engineering

    @Column(nullable = false, unique = true)
    private String code; // B.Tech - CSE

    @Column(nullable = false)
    private Integer durationYear;

    @Column(nullable = false)
    private Integer totalSemesters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    private boolean active = true;
}

