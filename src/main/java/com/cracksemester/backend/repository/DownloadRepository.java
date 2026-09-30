package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.Download;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {
    Long countByUserId(Long userId);
    Long countByResourceTypeAndResourceId(String resourceType, String resourceId);
}
