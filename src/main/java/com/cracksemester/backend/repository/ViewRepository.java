package com.cracksemester.backend.entity;

import com.cracksemester.backend.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    Long countByResourceTypeAndResourceId(String resourceType, String resourceId);
}
