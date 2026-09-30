package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.ReportEntity;
import com.cracksemester.backend.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, String> {
    List<ReportEntity> findByStatus(ReportStatus status);
    Long countByStatus(ReportStatus status);
}
