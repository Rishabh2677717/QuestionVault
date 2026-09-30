package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.entity.*;
import com.cracksemester.backend.enums.QuestionPaperStatus;
import com.cracksemester.backend.repository.*;
import com.cracksemester.backend.service.QuestionPaperService;
import com.cracksemester.backend.service.StorageService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionPaperServiceImpl implements QuestionPaperService {

    private final QuestionPaperRepository paperRepository;
    private final SubjectRepository subjectRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<QuestionPaperDTO> getQuestionPapers(int page, int size, String branch, Integer semester, Integer year, String examType, String status, String search, String sort) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("downloads".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "downloads");
        } else if ("views".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "views");
        } else if ("oldest".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Specification<QuestionPaper> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (branch != null && !branch.equalsIgnoreCase("ALL")) {
                predicates.add(cb.or(
                        cb.equal(cb.lower(root.get("branch").get("id")), branch.toLowerCase()),
                        cb.equal(cb.lower(root.get("branch").get("id")), "all")
                ));
            }

            if (semester != null && semester > 0) {
                predicates.add(cb.equal(root.get("semester").get("id"), semester));
            }

            if (year != null && year > 0) {
                predicates.add(cb.equal(root.get("examYear"), year));
            }

            if (examType != null && !examType.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.lower(root.get("examType")), examType.toLowerCase()));
            }

            if (status != null && !status.equalsIgnoreCase("ALL")) {
                try {
                    QuestionPaperStatus statusEnum = QuestionPaperStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (Exception ignored) {}
            }

            if (search != null && !search.trim().isEmpty()) {
                String q = "%" + search.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), q),
                        cb.like(cb.lower(root.get("subjectCode")), q),
                        cb.like(cb.lower(root.get("subjectName")), q)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<QuestionPaper> paperPage = paperRepository.findAll(spec, pageable);

        List<QuestionPaperDTO> content = paperPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<QuestionPaperDTO>builder()
                .content(content)
                .page(paperPage.getNumber())
                .size(paperPage.getSize())
                .totalElements(paperPage.getTotalElements())
                .totalPages(paperPage.getTotalPages())
                .last(paperPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionPaperDTO getQuestionPaperById(String id) {
        QuestionPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question paper not found with ID: " + id));
        return mapToDTO(paper);
    }

    @Override
    @Transactional
    public QuestionPaperDTO createQuestionPaper(QuestionPaperRequest request, MultipartFile file) {
        Subject subject = null;
        if (request.getSubjectCode() != null) {
            subject = subjectRepository.findByCodeIgnoreCase(request.getSubjectCode()).orElse(null);
        }
        if (subject == null) {
            subject = subjectRepository.findAll().stream().findFirst().orElse(null);
        }

        Branch branch = subject != null ? subject.getBranch() : null;
        if (branch == null && request.getBranch() != null) {
            branch = branchRepository.findById(request.getBranch()).orElse(null);
        }

        Semester semester = subject != null ? subject.getSemester() : null;
        if (semester == null && request.getSemester() != null) {
            semester = semesterRepository.findById(request.getSemester()).orElse(null);
        }

        String fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String fileName = "AKTU_" + request.getSubjectCode() + "_" + request.getYear() + ".pdf";
        String fileSize = "1.8 MB";

        if (file != null && !file.isEmpty()) {
            String storedName = storageService.storeFile(file);
            fileUrl = "/uploads/" + storedName;
            fileName = file.getOriginalFilename();
            fileSize = String.format("%.1f MB", file.getSize() / (1024.0 * 1024.0));
        }

        QuestionPaper paper = QuestionPaper.builder()
                .id("pyq-" + System.currentTimeMillis())
                .title(request.getTitle())
                .subject(subject)
                .subjectCode(subject.getCode())
                .subjectName(subject.getName())
                .branch(branch)
                .semester(semester)
                .academicYear(request.getAcademicYear() != null ? request.getAcademicYear() : "2025-26")
                .examYear(request.getYear())
                .examType(request.getExamType())
                .paperType(request.getPaperType() != null ? request.getPaperType() : "Regular")
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .pages(4)
                .downloads(0)
                .views(0)
                .status(QuestionPaperStatus.PUBLISHED)
                .description(request.getDescription())
                .uploadedBy("Admin")
                .publishedAt(LocalDateTime.now())
                .build();

        paperRepository.save(paper);
        return mapToDTO(paper);
    }

    @Override
    @Transactional
    public QuestionPaperDTO updateQuestionPaper(String id, QuestionPaperRequest request, MultipartFile file) {
        QuestionPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found with ID: " + id));

        if (request.getTitle() != null) paper.setTitle(request.getTitle());
        if (request.getYear() != null) paper.setExamYear(request.getYear());
        if (request.getExamType() != null) paper.setExamType(request.getExamType());
        if (request.getStatus() != null) {
            try {
                paper.setStatus(QuestionPaperStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (Exception ignored) {}
        }

        if (file != null && !file.isEmpty()) {
            String storedName = storageService.storeFile(file);
            paper.setFileUrl("/uploads/" + storedName);
            paper.setFileName(file.getOriginalFilename());
            paper.setFileSize(String.format("%.1f MB", file.getSize() / (1024.0 * 1024.0)));
        }

        paperRepository.save(paper);
        return mapToDTO(paper);
    }

    @Override
    @Transactional
    public void deleteQuestionPaper(String id) {
        paperRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionPaperDTO> getPopularPapers() {
        return paperRepository.findTop6ByStatusOrderByDownloadsDesc(QuestionPaperStatus.PUBLISHED)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionPaperDTO> getRecentPapers() {
        return paperRepository.findTop4ByStatusOrderByCreatedAtDesc(QuestionPaperStatus.PUBLISHED)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementDownload(String id) {
        paperRepository.findById(id).ifPresent(p -> {
            p.setDownloads(p.getDownloads() + 1);
            paperRepository.save(p);
        });
    }

    @Override
    @Transactional
    public void incrementView(String id) {
        paperRepository.findById(id).ifPresent(p -> {
            p.setViews(p.getViews() + 1);
            paperRepository.save(p);
        });
    }

    private QuestionPaperDTO mapToDTO(QuestionPaper p) {
        return QuestionPaperDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .subjectId(p.getSubject() != null ? p.getSubject().getId() : "")
                .subjectCode(p.getSubjectCode())
                .subjectName(p.getSubjectName())
                .branch(p.getBranch() != null ? p.getBranch().getId() : "CSE")
                .semester(p.getSemester() != null ? p.getSemester().getId() : 3)
                .academicYear(p.getAcademicYear() != null ? p.getAcademicYear() : "2025-26")
                .year(p.getExamYear())
                .examType(p.getExamType())
                .paperType(p.getPaperType())
                .fileUrl(p.getFileUrl())
                .fileName(p.getFileName())
                .fileSize(p.getFileSize())
                .pages(p.getPages())
                .downloads(p.getDownloads())
                .views(p.getViews())
                .status(p.getStatus() != null ? p.getStatus().name() : "PUBLISHED")
                .description(p.getDescription())
                .tags(Arrays.asList(p.getSubjectCode(), String.valueOf(p.getExamYear()), p.getBranch() != null ? p.getBranch().getId() : "CSE", "AKTU"))
                .uploadedBy(p.getUploadedBy())
                .uploadedDate(p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : "2025-01-01")
                .build();
    }
}
