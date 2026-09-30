package com.cracksemester.backend.config;

import com.cracksemester.backend.entity.University;
import com.cracksemester.backend.repository.UniversityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final UniversityRepository universityRepository;

    public DataSeeder(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    @Override
    public void run(String... args) {

        if (universityRepository.count() == 0) {

            University aktu = new University();
            aktu.setName("Dr. A.P.J. Abdul Kalam Technical University");

            universityRepository.save(aktu);

            System.out.println("✅ AKTU inserted successfully.");
        }
    }
}