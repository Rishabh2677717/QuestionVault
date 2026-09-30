package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.ApiResponse;
import com.cracksemester.backend.dto.SubjectDTO;
import com.cracksemester.backend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDTO>>> getSubjects(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String search
    ) {
        Integer semInt = parseInteger(semester);
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjects(branch, semInt, search)));
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
    public ResponseEntity<ApiResponse<SubjectDTO>> getSubjectById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectById(id)));
    }
}