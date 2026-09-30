package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getBranches();
    BranchDTO getBranchById(String id);
}