package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String>, JpaSpecificationExecutor<Subject> {
    Optional<Subject> findByCodeIgnoreCase(String code);
    Boolean existsByCodeIgnoreCase(String code);
    List<Subject> findByBranchIdAndSemesterId(String branchId, Integer semesterId);
    List<Subject> findByPopularTrue();
}