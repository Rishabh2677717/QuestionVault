package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.QuestionPaper;
import com.cracksemester.backend.enums.QuestionPaperStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, String>, JpaSpecificationExecutor<QuestionPaper> {
    List<QuestionPaper> findBySubjectCodeIgnoreCaseAndStatus(String subjectCode, QuestionPaperStatus status);
    List<QuestionPaper> findTop6ByStatusOrderByDownloadsDesc(QuestionPaperStatus status);
    List<QuestionPaper> findTop4ByStatusOrderByCreatedAtDesc(QuestionPaperStatus status);
    Long countByStatus(QuestionPaperStatus status);
    Long countBySubjectCodeIgnoreCaseAndExamYear(String subjectCode, Integer examYear);
}
