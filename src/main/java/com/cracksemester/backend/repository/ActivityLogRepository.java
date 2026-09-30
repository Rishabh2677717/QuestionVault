package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, String> {
    List<ActivityLogEntity> findTop20ByOrderByCreatedAtDesc();
}
