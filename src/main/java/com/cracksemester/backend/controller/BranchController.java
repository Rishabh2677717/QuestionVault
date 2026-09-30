package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.ApiResponse;
import com.cracksemester.backend.dto.BranchDTO;
import com.cracksemester.backend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchDTO>>> getBranches() {
        return ResponseEntity.ok(ApiResponse.success(branchService.getBranches()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDTO>> getBranchById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.getBranchById(id)));
    }
}