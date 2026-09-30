package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndResourceTypeAndResourceId(Long userId, String resourceType, String resourceId);
    Boolean existsByUserIdAndResourceTypeAndResourceId(Long userId, String resourceType, String resourceId);
    void deleteByUserIdAndResourceTypeAndResourceId(Long userId, String resourceType, String resourceId);
    Long countByUserId(Long userId);
}
