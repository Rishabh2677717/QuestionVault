package com.cracksemester.backend.config;

import com.cracksemester.backend.entity.Branch;
import com.cracksemester.backend.repository.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class BranchSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;

    public BranchSeeder(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public void run(String... args) {
        if (branchRepository.count() == 0) {
            List<Branch> branches = List.of(
                Branch.builder().id("CSE").name("Computer Science & Engineering").shortName("CSE").code("010").description("Software engineering, algorithms, data structures, database systems.").active(true).build(),
                Branch.builder().id("AIML").name("AI & Machine Learning").shortName("AI/ML").code("013").description("Artificial intelligence, deep learning, neural networks.").active(true).build(),
                Branch.builder().id("IT").name("Information Technology").shortName("IT").code("012").description("Information processing, network architecture, cloud computing.").active(true).build(),
                Branch.builder().id("ECE").name("Electronics & Comm. Engg.").shortName("ECE").code("031").description("VLSI design, digital signal processing, embedded systems.").active(true).build(),
                Branch.builder().id("EE").name("Electrical Engineering").shortName("EE").code("020").description("Power systems, electrical machines, control engineering.").active(true).build(),
                Branch.builder().id("ME").name("Mechanical Engineering").shortName("ME").code("040").description("Thermodynamics, fluid mechanics, CAD/CAM, machine design.").active(true).build(),
                Branch.builder().id("CE").name("Civil Engineering").shortName("CE").code("000").description("Structural engineering, surveying, geotechnical analysis.").active(true).build(),
                Branch.builder().id("DS").name("Data Science").shortName("DS").code("014").description("Big data analytics, data visualization, statistical modelling.").active(true).build()
            );

            branchRepository.saveAll(branches);
            System.out.println("✅ Branches verified/inserted.");
        }
    }
}