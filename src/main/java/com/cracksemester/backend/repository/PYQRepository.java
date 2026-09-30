package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.PYQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PYQRepository extends JpaRepository<PYQ, String> {
    List<PYQ> findBySubjectId(String subjectId);
}