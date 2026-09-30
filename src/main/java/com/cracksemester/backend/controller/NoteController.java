package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<NoteDTO>>> getNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String noteType,
            @RequestParam(defaultValue = "PUBLISHED") String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String subjectCode
    ) {
        Integer semInt = parseInteger(semester);
        String searchQuery = search != null ? search : subjectCode;
        PagedResponse<NoteDTO> result = noteService.getNotes(page, size, branch, semInt, noteType, status, searchQuery);
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteDTO>> getNoteById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getNoteById(id)));
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<ApiResponse<String>> trackDownload(@PathVariable String id) {
        noteService.incrementDownload(id);
        return ResponseEntity.ok(ApiResponse.success("Download recorded", id));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<String>> trackView(@PathVariable String id) {
        noteService.incrementView(id);
        return ResponseEntity.ok(ApiResponse.success("View recorded", id));
    }
}