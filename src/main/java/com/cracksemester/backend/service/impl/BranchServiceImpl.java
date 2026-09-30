package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.BranchDTO;
import com.cracksemester.backend.entity.Branch;
import com.cracksemester.backend.repository.BranchRepository;
import com.cracksemester.backend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDTO getBranchById(String id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + id));
        return mapToDTO(branch);
    }

    private BranchDTO mapToDTO(Branch b) {
        return BranchDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .shortName(b.getShortName())
                .code(b.getCode())
                .description(b.getDescription())
                .subjectCount(24L)
                .pyqCount(142L)
                .notesCount(88L)
                .status(b.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}