package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.ApiResponse;
import com.cracksemester.backend.dto.SemesterDTO;
import com.cracksemester.backend.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SemesterDTO>>> getSemesters() {
        return ResponseEntity.ok(ApiResponse.success(semesterService.getSemesters()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterDTO>> getSemesterById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(semesterService.getSemesterById(id)));
    }
}