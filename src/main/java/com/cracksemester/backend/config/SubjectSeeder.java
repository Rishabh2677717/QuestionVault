package com.cracksemester.backend.config;

import com.cracksemester.backend.entity.Branch;
import com.cracksemester.backend.entity.Semester;
import com.cracksemester.backend.entity.Subject;
import com.cracksemester.backend.repository.BranchRepository;
import com.cracksemester.backend.repository.SemesterRepository;
import com.cracksemester.backend.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class SubjectSeeder implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;

    public SubjectSeeder(SubjectRepository subjectRepository,
                         BranchRepository branchRepository,
                         SemesterRepository semesterRepository) {
        this.subjectRepository = subjectRepository;
        this.branchRepository = branchRepository;
        this.semesterRepository = semesterRepository;
    }

    @Override
    public void run(String... args) {
        if (subjectRepository.count() == 0) {
            Branch cse = branchRepository.findById("CSE").orElse(null);
            Semester sem3 = semesterRepository.findById(3).orElse(null);

            if (cse != null && sem3 != null) {
                Subject ds = Subject.builder()
                        .id("kcs301-data-structures")
                        .code("KCS301")
                        .name("Data Structures")
                        .branch(cse)
                        .semester(sem3)
                        .yearId(2)
                        .description("Arrays, Linked Lists, Stacks, Queues, Binary Trees, Graphs, Sorting & Searching.")
                        .popular(true)
                        .active(true)
                        .build();

                subjectRepository.save(ds);
                System.out.println("✅ Subjects verified/inserted.");
            }
        }
    }
}