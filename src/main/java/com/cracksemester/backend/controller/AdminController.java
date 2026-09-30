package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminDashboardService dashboardService;
    private final QuestionPaperService paperService;
    private final NoteService noteService;
    private final SubjectService subjectService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getContentHealth() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getContentHealth()));
    }

    @GetMapping("/content-gaps")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getContentGaps() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getContentGaps()));
    }

    @GetMapping("/broken-files")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBrokenFiles() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getBrokenFiles()));
    }

    @GetMapping("/duplicates")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDuplicates() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDuplicates()));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReports() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getReports()));
    }

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveReport(@PathVariable String id) {
        dashboardService.resolveReport(id);
        return ResponseEntity.ok(ApiResponse.success("Report resolved", id));
    }

    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActivityLogs() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getActivityLogs()));
    }

    @GetMapping("/analytics/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSearchAnalytics()));
    }

    // Admin Question Paper Upload & Management
    @PostMapping(value = "/question-papers", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<ApiResponse<QuestionPaperDTO>> uploadQuestionPaper(
            @RequestPart(value = "data", required = false) QuestionPaperRequest dataPart,
            @RequestBody(required = false) QuestionPaperRequest bodyPart,
            @ModelAttribute QuestionPaperRequest formPart,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        QuestionPaperRequest request = dataPart != null ? dataPart : (bodyPart != null && bodyPart.getTitle() != null ? bodyPart : formPart);
        QuestionPaperDTO dto = paperService.createQuestionPaper(request, file);
        return ResponseEntity.ok(ApiResponse.success("Question Paper uploaded successfully", dto));
    }

    @PutMapping(value = "/question-papers/{id}", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<ApiResponse<QuestionPaperDTO>> updateQuestionPaper(
            @PathVariable String id,
            @RequestPart(value = "data", required = false) QuestionPaperRequest dataPart,
            @RequestBody(required = false) QuestionPaperRequest bodyPart,
            @ModelAttribute QuestionPaperRequest formPart,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        QuestionPaperRequest request = dataPart != null ? dataPart : (bodyPart != null && bodyPart.getTitle() != null ? bodyPart : formPart);
        QuestionPaperDTO dto = paperService.updateQuestionPaper(id, request, file);
        return ResponseEntity.ok(ApiResponse.success("Question Paper updated", dto));
    }

    @DeleteMapping("/question-papers/{id}")
    public ResponseEntity<ApiResponse<String>> deleteQuestionPaper(@PathVariable String id) {
        paperService.deleteQuestionPaper(id);
        return ResponseEntity.ok(ApiResponse.success("Question paper deleted", id));
    }

    // Admin Notes Upload & Management
    @PostMapping(value = "/notes", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<ApiResponse<NoteDTO>> uploadNote(
            @RequestPart(value = "data", required = false) NoteRequest dataPart,
            @RequestBody(required = false) NoteRequest bodyPart,
            @ModelAttribute NoteRequest formPart,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        NoteRequest request = dataPart != null ? dataPart : (bodyPart != null && bodyPart.getTitle() != null ? bodyPart : formPart);
        NoteDTO dto = noteService.createNote(request, file);
        return ResponseEntity.ok(ApiResponse.success("Study notes uploaded successfully", dto));
    }

    @PutMapping(value = "/notes/{id}", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<ApiResponse<NoteDTO>> updateNote(
            @PathVariable String id,
            @RequestPart(value = "data", required = false) NoteRequest dataPart,
            @RequestBody(required = false) NoteRequest bodyPart,
            @ModelAttribute NoteRequest formPart,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        NoteRequest request = dataPart != null ? dataPart : (bodyPart != null && bodyPart.getTitle() != null ? bodyPart : formPart);
        NoteDTO dto = noteService.updateNote(id, request, file);
        return ResponseEntity.ok(ApiResponse.success("Study notes updated", dto));
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNote(@PathVariable String id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.success("Study notes deleted", id));
    }

    // Admin Subject Management
    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<SubjectDTO>> createSubject(@Valid @RequestBody SubjectDTO dto) {
        SubjectDTO result = subjectService.createSubject(dto);
        return ResponseEntity.ok(ApiResponse.success("Subject created", result));
    }
}
