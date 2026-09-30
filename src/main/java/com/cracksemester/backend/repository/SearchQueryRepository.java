package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.SearchQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchQueryRepository extends JpaRepository<SearchQueryEntity, Long> {
    
    @Query("SELECT s.query, COUNT(s) as cnt FROM SearchQueryEntity s GROUP BY s.query ORDER BY cnt DESC")
    List<Object[]> findTopSearchedQueries();

    @Query("SELECT s.query, COUNT(s) as cnt FROM SearchQueryEntity s WHERE s.resultCount = 0 GROUP BY s.query ORDER BY cnt DESC")
    List<Object[]> findZeroResultSearches();
}
