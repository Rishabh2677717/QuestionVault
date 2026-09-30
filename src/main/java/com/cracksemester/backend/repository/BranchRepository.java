package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, String> {
    List<Branch> findByActiveTrue();
    Boolean existsByCode(String code);
}