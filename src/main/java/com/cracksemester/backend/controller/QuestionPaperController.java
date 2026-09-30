package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.service.QuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-papers")
@RequiredArgsConstructor
public class QuestionPaperController {

    private final QuestionPaperService paperService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<QuestionPaperDTO>>> getQuestionPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String examType,
            @RequestParam(defaultValue = "PUBLISHED") String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) String subjectCode
    ) {
        Integer semInt = parseInteger(semester);
        Integer yearInt = parseInteger(year);
        String searchQuery = search != null ? search : subjectCode;
        PagedResponse<QuestionPaperDTO> result = paperService.getQuestionPapers(page, size, branch, semInt, yearInt, examType, status, searchQuery, sort);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private Integer parseInteger(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<QuestionPaperDTO>>> getPopularPapers() {
        return ResponseEntity.ok(ApiResponse.success(paperService.getPopularPapers()));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<QuestionPaperDTO>>> getRecentPapers() {
        return ResponseEntity.ok(ApiResponse.success(paperService.getRecentPapers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionPaperDTO>> getQuestionPaperById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(paperService.getQuestionPaperById(id)));
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<ApiResponse<String>> trackDownload(@PathVariable String id) {
        paperService.incrementDownload(id);
        return ResponseEntity.ok(ApiResponse.success("Download recorded", id));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<String>> trackView(@PathVariable String id) {
        paperService.incrementView(id);
        return ResponseEntity.ok(ApiResponse.success("View recorded", id));
    }
}