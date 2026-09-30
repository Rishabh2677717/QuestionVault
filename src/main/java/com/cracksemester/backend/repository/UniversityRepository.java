package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
}