package com.cracksemester.backend.config;

import com.cracksemester.backend.entity.Semester;
import com.cracksemester.backend.repository.SemesterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class SemesterSeeder implements CommandLineRunner {

    private final SemesterRepository semesterRepository;

    public SemesterSeeder(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @Override
    public void run(String... args) {
        if (semesterRepository.count() == 0) {
            List<Semester> semesters = List.of(
                Semester.builder().id(1).name("Semester 1").shortName("Sem 1").yearGroup("1st Year").active(true).build(),
                Semester.builder().id(2).name("Semester 2").shortName("Sem 2").yearGroup("1st Year").active(true).build(),
                Semester.builder().id(3).name("Semester 3").shortName("Sem 3").yearGroup("2nd Year").active(true).build(),
                Semester.builder().id(4).name("Semester 4").shortName("Sem 4").yearGroup("2nd Year").active(true).build(),
                Semester.builder().id(5).name("Semester 5").shortName("Sem 5").yearGroup("3rd Year").active(true).build(),
                Semester.builder().id(6).name("Semester 6").shortName("Sem 6").yearGroup("3rd Year").active(true).build(),
                Semester.builder().id(7).name("Semester 7").shortName("Sem 7").yearGroup("4th Year").active(true).build(),
                Semester.builder().id(8).name("Semester 8").shortName("Sem 8").yearGroup("4th Year").active(true).build()
            );

            semesterRepository.saveAll(semesters);
            System.out.println("✅ Semesters verified/inserted.");
        }
    }
}