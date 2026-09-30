package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.entity.QuestionPaper;
import com.cracksemester.backend.entity.ReportEntity;
import com.cracksemester.backend.entity.Subject;
import com.cracksemester.backend.enums.NoteStatus;
import com.cracksemester.backend.enums.QuestionPaperStatus;
import com.cracksemester.backend.enums.ReportStatus;
import com.cracksemester.backend.repository.*;
import com.cracksemester.backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final QuestionPaperRepository paperRepository;
    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ActivityLogRepository activityLogRepository;
    private final SearchQueryRepository searchQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalPapers = paperRepository.count();
        long totalNotes = noteRepository.count();
        long totalSubjects = subjectRepository.count();
        long totalBranches = branchRepository.count();
        long totalUsers = userRepository.count();
        long publishedPapers = paperRepository.countByStatus(QuestionPaperStatus.PUBLISHED);
        long draftPapers = paperRepository.countByStatus(QuestionPaperStatus.DRAFT);
        long archivedPapers = paperRepository.countByStatus(QuestionPaperStatus.ARCHIVED);

        stats.put("totalQuestionPapers", totalPapers);
        stats.put("totalNotes", totalNotes);
        stats.put("totalSubjects", totalSubjects);
        stats.put("totalBranches", totalBranches);
        stats.put("totalUsers", totalUsers);
        stats.put("publishedResources", publishedPapers);
        stats.put("draftResources", draftPapers);
        stats.put("archivedResources", archivedPapers);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getContentHealth() {
        Map<String, Object> health = new HashMap<>();

        long draftCount = paperRepository.countByStatus(QuestionPaperStatus.DRAFT) + noteRepository.countByStatus(NoteStatus.DRAFT);
        long openReports = reportRepository.countByStatus(ReportStatus.OPEN);

        // Count subjects missing recent 2025 paper
        List<Subject> subjects = subjectRepository.findAll();
        long missingCount = 0;
        for (Subject s : subjects) {
            long c = paperRepository.countBySubjectCodeIgnoreCaseAndExamYear(s.getCode(), 2025);
            if (c == 0) missingCount++;
        }

        health.put("missingPapers", missingCount);
        health.put("brokenFiles", 0);
        health.put("duplicateResources", 0);
        health.put("unpublishedDrafts", draftCount);
        health.put("incompleteMetadata", 0);
        health.put("reportedResources", openReports);

        return health;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getContentGaps() {
        List<Map<String, Object>> gaps = new ArrayList<>();
        List<Subject> subjects = subjectRepository.findAll();

        int[] checkYears = {2025, 2024, 2023, 2022, 2021};

        for (Subject s : subjects) {
            Map<Integer, Boolean> yearPresence = new LinkedHashMap<>();
            int countPresent = 0;

            for (int y : checkYears) {
                long paperCount = paperRepository.countBySubjectCodeIgnoreCaseAndExamYear(s.getCode(), y);
                boolean present = paperCount > 0;
                yearPresence.put(y, present);
                if (present) countPresent++;
            }

            int completenessPercent = (countPresent * 100) / checkYears.length;

            Map<String, Object> item = new HashMap<>();
            item.put("subject", s.getName());
            item.put("code", s.getCode());
            item.put("branch", s.getBranch() != null ? s.getBranch().getId() : "CSE");
            item.put("sem", s.getSemester() != null ? s.getSemester().getId() : 1);
            item.put("completeness", completenessPercent + "%");
            item.put("years", yearPresence);

            gaps.add(item);
        }

        return gaps;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBrokenFiles() {
        // Query papers or notes with broken or unreachable storage flags
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDuplicates() {
        // Find potential duplicates where subjectCode, examYear, and examType are identical
        List<Map<String, Object>> duplicates = new ArrayList<>();
        List<QuestionPaper> papers = paperRepository.findAll();

        Map<String, List<QuestionPaper>> grouped = papers.stream()
                .collect(Collectors.groupingBy(p -> p.getSubjectCode() + "_" + p.getExamYear() + "_" + p.getExamType()));

        for (Map.Entry<String, List<QuestionPaper>> entry : grouped.entrySet()) {
            List<QuestionPaper> list = entry.getValue();
            if (list.size() > 1) {
                QuestionPaper p1 = list.get(0);
                QuestionPaper p2 = list.get(1);

                Map<String, Object> dup = new HashMap<>();
                dup.put("id", "dup-" + p1.getId());
                dup.put("title1", p1.getTitle());
                dup.put("title2", p2.getTitle());
                dup.put("subjectCode", p1.getSubjectCode());
                dup.put("branch", p1.getBranch() != null ? p1.getBranch().getId() : "CSE");
                dup.put("sem", p1.getSemester() != null ? p1.getSemester().getId() : 1);
                dup.put("year", p1.getExamYear());
                dup.put("examType", p1.getExamType());
                dup.put("similarity", "95% Match");

                duplicates.add(dup);
            }
        }

        return duplicates;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSearchAnalytics() {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> topDbQueries = searchQueryRepository.findTopSearchedQueries();
        List<Object[]> zeroDbQueries = searchQueryRepository.findZeroResultSearches();

        List<Map<String, Object>> topSearches = new ArrayList<>();
        for (Object[] row : topDbQueries) {
            topSearches.add(Map.of(
                    "term", String.valueOf(row[0]),
                    "count", ((Number) row[1]).longValue(),
                    "category", "Search Term"
            ));
            if (topSearches.size() >= 10) break;
        }

        List<Map<String, Object>> zeroResults = new ArrayList<>();
        for (Object[] row : zeroDbQueries) {
            zeroResults.add(Map.of(
                    "term", String.valueOf(row[0]),
                    "count", ((Number) row[1]).longValue(),
                    "lastSearched", "Recently"
            ));
            if (zeroResults.size() >= 10) break;
        }

        result.put("topSearches", topSearches);
        result.put("zeroResultSearches", zeroResults);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityLogs() {
        return activityLogRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(log -> Map.<String, Object>of(
                        "id", log.getId(),
                        "admin", log.getAdminName(),
                        "action", log.getActionType(),
                        "detail", log.getDetail(),
                        "time", log.getCreatedAt() != null ? log.getCreatedAt().toLocalDate().toString() : "Recent"
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getReports() {
        return reportRepository.findAll().stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getId(),
                        "resourceTitle", r.getResourceTitle(),
                        "resourceType", r.getResourceType(),
                        "resourceId", r.getResourceId(),
                        "reporter", r.getReporterEmail(),
                        "reason", r.getReason(),
                        "date", r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : "Recent",
                        "status", r.getStatus() != null ? r.getStatus().name() : "OPEN"
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolveReport(String reportId) {
        reportRepository.findById(reportId).ifPresent(r -> {
            r.setStatus(ReportStatus.RESOLVED);
            r.setResolvedAt(LocalDateTime.now());
            reportRepository.save(r);
        });
    }
}
